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
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import br.udesc.ddm.controlefinanceiro.MainActivity
import br.udesc.ddm.controlefinanceiro.R
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.databinding.ActivityDetalhesTipoLancamentoBinding
import br.udesc.ddm.controlefinanceiro.model.TipoLancamento
import kotlinx.coroutines.launch

class DetalhesTipoLancamentoActivity : AppCompatActivity() {

    private var tipoLancamentoId: Long = 0L
    private var tipoLancamento: TipoLancamento? = null
    private val binding by lazy {
        ActivityDetalhesTipoLancamentoBinding.inflate(layoutInflater)
    }
    private val tipoLancamentoDao by lazy {
        AppDatabase.instancia(this).tipoLancamentoDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        tentaCarregarTipoLancamento()

        // Cria o canal de notificação (necessário apenas uma vez)
        createNotificationChannel(this)
    }

    override fun onResume() {
        super.onResume()
        buscaTipoLancamento()
    }

    private fun buscaTipoLancamento() {
        lifecycleScope.launch {
            tipoLancamentoDao.buscaPorId(tipoLancamentoId).collect { tipoLancamentoEncontrado ->
                tipoLancamento = tipoLancamentoEncontrado
                tipoLancamento?.let {
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
                tipoLancamento?.let {
                    lifecycleScope.launch {
                        tipoLancamentoDao.remove(it)
                        finish()
                    }
                }

                // Exibe a notificação
                val titulo = "Tipo removido"
                val texto = "Este tipo de lançamento foi excluído com sucesso."
                showNotification(this, titulo, texto)
            }

            R.id.menu_detalhes_editar -> {
                Intent(this, CadastrarTipoLancamentoActivity::class.java).apply {
                    putExtra(CHAVE_TIPOLANCAMENTO_ID, tipoLancamentoId)
                    startActivity(this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun tentaCarregarTipoLancamento() {
        tipoLancamentoId = intent.getLongExtra(CHAVE_TIPOLANCAMENTO_ID, 0L)
    }

    private fun preencheCampos(tipoLancamentoCarregado: TipoLancamento) {
        with(binding) {
            activityDetalhesTipolancamentoNome.text = tipoLancamentoCarregado.nome
            activityDetalhesTipolancamentoAcao.text = tipoLancamentoCarregado.acao
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