package br.udesc.ddm.controlefinanceiro.ui.activity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import br.udesc.ddm.controlefinanceiro.MainActivity
import br.udesc.ddm.controlefinanceiro.R
import br.udesc.ddm.controlefinanceiro.databinding.ActivityDetalhesLancamentoBinding
import br.udesc.ddm.controlefinanceiro.model.Lancamento
import br.udesc.ddm.controlefinanceiro.viewModel.LancamentoViewModel
import kotlinx.coroutines.launch

class DetalhesLancamentoActivity : AppCompatActivity() {

    private var lancamentoId: Long = 0L
    private var lancamento: Lancamento? = null
    private val binding by lazy {
        ActivityDetalhesLancamentoBinding.inflate(layoutInflater)
    }

    private lateinit var lancamentoViewModel: LancamentoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lancamentoViewModel = ViewModelProvider(this).get(LancamentoViewModel::class.java)
        setContentView(binding.root)
        tentaCarregarLancamento()

        // Cria o canal de notificação (necessário apenas uma vez)
        createNotificationChannel(this)
    }

    override fun onResume() {
        super.onResume()
        buscaLancamento()
    }

    private fun buscaLancamento() {
        lifecycleScope.launch {
            lancamentoViewModel.buscaPorId(lancamentoId).collect { lancamentoCarregado ->
                lancamento = lancamentoCarregado
                lancamento?.let {
                    preencheCampos(it)
                } ?: finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detalhes_projeto, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_detalhes_remover -> {
                lancamento?.let {
                    lifecycleScope.launch {
                        lancamentoViewModel.remove(it)
                        finish()
                    }
                }

                // Exibe a notificação
                val titulo = "Lançamento removido"
                val texto = "Este lançamento foi excluído com sucesso."
                showNotification(this, titulo, texto)
            }

            R.id.menu_detalhes_editar -> {
                Intent(this, CadastrarLancamentoActivity::class.java).apply {
                    putExtra(CHAVE_LANCAMENTO_ID, lancamentoId)
                    startActivity(this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun tentaCarregarLancamento() {
        lancamentoId = intent.getLongExtra(CHAVE_LANCAMENTO_ID, 0L)
    }

    private fun preencheCampos(lancamentoCarregado: Lancamento) {
        with(binding) {
            lancamentoCarregado.diretorioImagem?.let { caminhoFoto ->
                val bitmap = BitmapFactory.decodeFile(caminhoFoto)
                activityDetalhesLancamentoImagem.setImageBitmap(bitmap)
            }

//            activityDetalhesLancamentoImagem.tentaCarregarImagem(lancamentoCarregado.diretorioImagem)
            activityDetalhesLancamentoNome.text = lancamentoCarregado.nome
            activityDetalhesLancamentoTipogasto.text = lancamentoCarregado.tipo
            activityDetalhesLancamentoConta.text = lancamentoCarregado.banco
            activityDetalhesLancamentoValor.text = lancamentoCarregado.valor.toString()
        }
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "CHANEL_ID"
            val channelName = "Canal"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Descrição do canal"
            }

            // Registra o canal no sistema
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //    @SuppressLint("MissingPermission")
    fun showNotification(context: Context, titulo: String, texto: String) {
        val channelId = "CHANEL_ID" // Deve corresponder ao canal criado
        val notificationId = 1 // ID único da notificação

        // Intent para abrir uma Activity quando a notificação for clicada
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = getActivity(context, 0, intent, FLAG_UPDATE_CURRENT)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Cria a notificação
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_action_add) // Ícone da notificação
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Prioridade
//            .setContentIntent(pendingIntent) // Intent de clique
//            .setAutoCancel(true) // Fecha a notificação após o clique

        // Exibe a notificação
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

}