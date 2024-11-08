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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import br.udesc.ddm.controlefinanceiro.MainActivity
import br.udesc.ddm.controlefinanceiro.R
import br.udesc.ddm.controlefinanceiro.databinding.ActivityDetalhesContaBinding
import br.udesc.ddm.controlefinanceiro.extensions.tentaCarregarImagem
import br.udesc.ddm.controlefinanceiro.model.Conta
import br.udesc.ddm.controlefinanceiro.viewModel.ContaViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal

class DetalhesContaActivity : AppCompatActivity() {

    private var contaId: Long = 0L
    private var conta: Conta? = null
    private var contaSaldo: BigDecimal? = BigDecimal.ZERO
    private val binding by lazy {
        ActivityDetalhesContaBinding.inflate(layoutInflater)
    }

    private lateinit var contaViewModel: ContaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contaViewModel = ViewModelProvider(this).get(ContaViewModel::class.java)
        setContentView(binding.root)
        tentaCarregarConta()

        // Cria o canal de notificação (necessário apenas uma vez)
        createNotificationChannel(this)
    }

    override fun onResume() {
        super.onResume()
        buscaConta()
    }

    private fun buscaConta() {
        lifecycleScope.launch {
            contaViewModel.buscaPorId(contaId).collect { contaEncontrada ->
                conta = contaEncontrada
                conta?.let {
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
                conta?.let {
                    lifecycleScope.launch {
                        contaViewModel.remove(it)
                        finish()
                    }
                }

                // Exibe a notificação
                val titulo = "Conta removida"
                val texto = "Esta conta foi excluída com sucesso."
                showNotification(this, titulo, texto)

            }

            R.id.menu_detalhes_editar -> {
                Intent(this, CadastrarContaActivity::class.java).apply {
                    putExtra(CHAVE_CONTA_ID, contaId)
                    startActivity(this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun tentaCarregarConta() {
        contaId = intent.getLongExtra(CHAVE_CONTA_ID, 0L)
    }

    private fun preencheCampos(contaCarregada: Conta) {

        contaSaldo = contaViewModel.getSaldoConta(contaId)

        with(binding) {
            activityDetalhesContaImagem.tentaCarregarImagem(contaCarregada.imagem)
            activityDetalhesContaNome.text = contaCarregada.nome
            activityDetalhesContaSaldo.text = contaSaldo.toString()
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