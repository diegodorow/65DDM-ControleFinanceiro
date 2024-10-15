package br.udesc.ddm.controlefinanceiro.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.udesc.ddm.controlefinanceiro.R
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.databinding.ActivityDetalhesContaBinding
import br.udesc.ddm.controlefinanceiro.extensions.tentaCarregarImagem
import br.udesc.ddm.controlefinanceiro.model.Conta
import kotlinx.coroutines.launch
import java.math.BigDecimal

class DetalhesContaActivity : AppCompatActivity() {

    private var contaId: Long = 0L
    private var conta: Conta? = null
    private var contaSaldo: BigDecimal? = BigDecimal.ZERO
    private val binding by lazy {
        ActivityDetalhesContaBinding.inflate(layoutInflater)
    }
    private val contaDao by lazy {
        AppDatabase.instancia(this).contaDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        tentaCarregarConta()
    }

    override fun onResume() {
        super.onResume()
        buscaConta()
    }

    private fun buscaConta() {
        lifecycleScope.launch {
            contaDao.buscaPorId(contaId).collect { contaEncontrada ->
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
                        contaDao.remove(it)
                        finish()
                    }
                }

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

        contaSaldo = contaDao.buscaSaldo(contaId)

        with(binding) {
            activityDetalhesContaImagem.tentaCarregarImagem(contaCarregada.imagem)
            activityDetalhesContaNome.text = contaCarregada.nome
            activityDetalhesContaSaldo.text = contaSaldo.toString()
        }
    }
}