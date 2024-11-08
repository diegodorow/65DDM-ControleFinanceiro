package br.udesc.ddm.controlefinanceiro.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import br.udesc.ddm.controlefinanceiro.databinding.FragmentCadastrarLancamentoBinding
import br.udesc.ddm.controlefinanceiro.model.Lancamento
import br.udesc.ddm.controlefinanceiro.viewModel.ContaViewModel
import br.udesc.ddm.controlefinanceiro.viewModel.LancamentoViewModel
import br.udesc.ddm.controlefinanceiro.viewModel.TipoLancamentoViewModel
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal

class CadastrarLancamentoFragment : Fragment() {
    private var _binding: FragmentCadastrarLancamentoBinding? = null
    private lateinit var lancamentoViewModel: LancamentoViewModel
    private lateinit var contaViewModel: ContaViewModel
    private lateinit var tipoLancamentoViewModel: TipoLancamentoViewModel

    private val binding get() = _binding!!

    private lateinit var botaoCadastrar: Button
    private var lancamentoId = 0L

    private lateinit var previewView: PreviewView
    private var imageCapture: ImageCapture? = null
    private lateinit var imageViewFoto: ImageView
    private var isCameraReady = false
    private lateinit var currentPhotoPath: String
    private lateinit var botaoAbrirCamera: Button


    private lateinit var listaConta: List<String>
    private lateinit var listaTiposGasto: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lancamentoViewModel = ViewModelProvider(this).get(LancamentoViewModel::class.java)
        contaViewModel = ViewModelProvider(this).get(ContaViewModel::class.java)
        tipoLancamentoViewModel = ViewModelProvider(this).get(TipoLancamentoViewModel::class.java)

        _binding = FragmentCadastrarLancamentoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        botaoCadastrar = binding.fragmentFormularioLancamentoBotaoSalvar

        listaConta = contaViewModel.buscaTodosParaSpinner()
        listaTiposGasto = tipoLancamentoViewModel.buscaTodosParaSpinner()

        previewView = binding.previewView
        imageViewFoto = binding.imageViewFoto
        botaoAbrirCamera = binding.fragmentFormularioLancamentoBotaoCapturarImagem
        currentPhotoPath = ""

        configuraBotaoSalvar()
        configuraSpinner()
        startCamera()

        if (!allPermissionsGranted()) {
            requestPermissions()
        }

        botaoAbrirCamera.setOnClickListener {
            takePhoto()
        }

        return root
    }

    private fun configuraBotaoSalvar() {
        val botaoSalvar = binding.fragmentFormularioLancamentoBotaoSalvar

        botaoSalvar.setOnClickListener {
            val lancamentoNovo = criaLancamento()
            if (lancamentoNovo.nome.isNotEmpty()) {
                lancamentoViewModel.cadastrarLancamento(lancamentoNovo)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Preencha todos os campos!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            Navigation.findNavController(binding.root)
                .navigate(br.udesc.ddm.controlefinanceiro.R.id.nav_lista_lancamentos)
        }
    }

    private fun verificaSaldoConta(contaId: Long) {
        val saldoConta = contaViewModel.getSaldoConta(contaId);
        if (saldoConta <= BigDecimal.ZERO) {

        }
    }

    private fun criaLancamento(): Lancamento {
        val campoNome = binding.fragmentCadastroLancamentoNome
        val nome = campoNome.text.toString()
        val conta = binding.spConta.selectedItem.toString()
        val tipoGasto = binding.spTipoGasto.selectedItem.toString()

        val campoValor = binding.fragmentCadastroLancamentoValor
        val valorEmTexto = campoValor.text.toString()
        val valor = if (valorEmTexto.isBlank()) {
            BigDecimal.ZERO
        } else {
            BigDecimal(valorEmTexto)
        }

        val textoDiretorioImagem = currentPhotoPath

        Log.i("CadastrarLancamentoActivity", textoDiretorioImagem)

        return Lancamento(
            id = lancamentoId,
            nome = nome,
            banco = conta,
            tipo = tipoGasto,
            valor = valor,
            diretorioImagem = textoDiretorioImagem
        )
    }

    private fun configuraSpinner() {

        val spListaConta = binding.spConta

        val adapterListaConta = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listaConta
        )

        adapterListaConta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spListaConta.adapter = adapterListaConta

        val spListaTipoGasto = binding.spTipoGasto

        val adapterListaTipoGasto = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listaTiposGasto
        )

        adapterListaTipoGasto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spListaTipoGasto.adapter = adapterListaTipoGasto
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                previewView.visibility = View.VISIBLE
                imageViewFoto.visibility = View.GONE
            } catch (exc: Exception) {
                Log.e("CameraXApp", "Erro ao inicializar a câmera: ${exc.message}", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            requireContext().externalMediaDirs.firstOrNull(),
            "${System.currentTimeMillis()}.jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        Log.i("CameraApp", "takePhoto")
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {

                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraApp", "Erro ao capturar imagem: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.i("CameraApp", "onImageSaved")
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    val rotatedBitmap = rotateBitmapFixed(bitmap)
                    imageViewFoto.setImageBitmap(rotatedBitmap)
                    imageViewFoto.visibility = View.VISIBLE
                    previewView.visibility = View.GONE

                    currentPhotoPath = saveBitmapToExternalStorage(rotatedBitmap) ?: ""
                }
            }
        )

        Log.i("CameraApp", "currentPhotoPath = " + currentPhotoPath)
    }

    private fun rotateBitmapFixed(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            10
        )
    }

    private fun saveBitmapToExternalStorage(bitmap: Bitmap): String? {
        val context = requireContext()
        val photoDir = File(context.getExternalFilesDir(null), "lancamento_fotos")

        if (!photoDir.exists()) {
            photoDir.mkdirs()
        }

        val photoFile = File(photoDir, "lancamento_${System.currentTimeMillis()}.jpg")
        return try {
            val outputStream = FileOutputStream(photoFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()
            photoFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}