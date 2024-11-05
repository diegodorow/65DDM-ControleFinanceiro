package br.udesc.ddm.controlefinanceiro.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.database.dao.ContaDao
import br.udesc.ddm.controlefinanceiro.database.dao.LancamentoDao
import br.udesc.ddm.controlefinanceiro.database.dao.TipoLancamentoDao
import br.udesc.ddm.controlefinanceiro.databinding.ActivityCadastrarLancamentoBinding
import br.udesc.ddm.controlefinanceiro.model.Lancamento
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal

class CadastrarLancamentoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastrarLancamentoBinding.inflate(layoutInflater)
    }

    private val lancamentoDao: LancamentoDao by lazy {
        val db = AppDatabase.instancia(this)
        db.lancamentoDao()
    }

    private val tipoLancamentoDao: TipoLancamentoDao by lazy {
        val db = AppDatabase.instancia(this)
        db.tipoLancamentoDao()
    }

    private val contaDao: ContaDao by lazy {
        val db = AppDatabase.instancia(this)
        db.contaDao()
    }

    private lateinit var previewView: PreviewView
    private var imageCapture: ImageCapture? = null
    private lateinit var imageViewFoto: ImageView
    private var isCameraReady = false
    private lateinit var currentPhotoPath: String
    private lateinit var botaoAbrirCamera: Button

    private var lancamentoId = 0L
    val listaConta = contaDao.buscaTodosSpinner()
    val listaTiposGasto = tipoLancamentoDao.buscaTodosSpinner()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Cadastrar lançamento"
        previewView = binding.previewView
        imageViewFoto = binding.imageViewFoto
        botaoAbrirCamera = binding.activityFormularioLancamentoBotaoCapturarImagem
        configuraSpinner()
        configuraBotaoSalvar()
        tentaCarregarLancamento()

        startCamera()

        botaoAbrirCamera.setOnClickListener {
            takePhoto()
        }
    }

    override fun onResume() {
        super.onResume()
        tentaBuscarLancamento()
    }

    private fun configuraBotaoSalvar() {
        val botaoSalvar = binding.activityFormularioLancamentoBotaoSalvar

        botaoSalvar.setOnClickListener {
            lifecycleScope.launch {
                val lancamentoNovo = criaLancamento()
                lancamentoDao.salva(lancamentoNovo)
                finish()
            }
        }
    }

    private fun criaLancamento(): Lancamento {
        val campoNome = binding.activityCadastroLancamentoNome
        val nome = campoNome.text.toString()
        val conta = binding.spConta.selectedItem.toString()
        val tipoGasto = binding.spTipoGasto.selectedItem.toString()

        val campoValor = binding.activityCadastroLancamentoValor
        val valorEmTexto = campoValor.text.toString()
        val valor = if (valorEmTexto.isBlank()) {
            BigDecimal.ZERO
        } else {
            BigDecimal(valorEmTexto)
        }

        currentPhotoPath = ""
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

    private fun tentaCarregarLancamento() {
        lancamentoId = intent.getLongExtra(CHAVE_LANCAMENTO_ID, 0L)
    }

    private fun tentaBuscarLancamento() {
        lifecycleScope.launch {
            lancamentoDao.buscaPorId(lancamentoId).collect {
                it?.let { lancamentoEncontrado ->
                    title = "Alterar lançamento"
                    preencheCampos(lancamentoEncontrado)
                }
            }
        }
    }

    private fun preencheCampos(lancamento: Lancamento) {
        binding.activityCadastroLancamentoNome.setText(lancamento.nome)
        binding.activityCadastroLancamentoValor.setText(lancamento.valor.toString())

        val idConta: Int = listaConta.indexOf(lancamento.banco)
        binding.spConta.setSelection(idConta)
        Log.i("CadastrarLancamentoActivity", "preencheCampos: idConta=$idConta")

        val idTipoGasto: Int = listaTiposGasto.indexOf(lancamento.tipo)
        binding.spTipoGasto.setSelection(idTipoGasto)
        Log.i("CadastrarLancamentoActivity", "preencheCampos: idTipoGasto=$idTipoGasto")
    }

    private fun configuraSpinner() {

        val spListaConta = binding.spConta
//        val listaConta = contaDao.buscaTodosSpinner()
        val adapterListaConta =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaConta)
        adapterListaConta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spListaConta.adapter = adapterListaConta

        val spListaTipoGasto = binding.spTipoGasto
//        val listaTiposGasto = tipoLancamentoDao.buscaTodosSpinner()
        val adapterListaTipoGasto =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaTiposGasto)
        adapterListaTipoGasto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spListaTipoGasto.adapter = adapterListaTipoGasto
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
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
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile =
            File(this.externalMediaDirs.firstOrNull(), "${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraApp", "Erro ao capturar imagem: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    val rotatedBitmap = rotateBitmapFixed(bitmap)
                    imageViewFoto.setImageBitmap(rotatedBitmap)
                    imageViewFoto.visibility = View.VISIBLE
                    previewView.visibility = View.GONE

                    currentPhotoPath = saveBitmapToExternalStorage(rotatedBitmap) ?: ""
                }
            }
        )
    }

    private fun rotateBitmapFixed(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
    }

    private fun saveBitmapToExternalStorage(bitmap: Bitmap): String? {
        val context = this
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