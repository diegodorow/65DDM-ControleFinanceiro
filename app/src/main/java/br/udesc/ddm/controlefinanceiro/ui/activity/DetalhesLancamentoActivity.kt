package br.udesc.ddm.controlefinanceiro.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.udesc.ddm.controlefinanceiro.R
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.databinding.ActivityDetalhesLancamentoBinding
import br.udesc.ddm.controlefinanceiro.model.Lancamento
import kotlinx.coroutines.launch

class DetalhesLancamentoActivity : AppCompatActivity() {

    private var lancamentoId: Long = 0L
    private var lancamento: Lancamento? = null
    private val binding by lazy {
        ActivityDetalhesLancamentoBinding.inflate(layoutInflater)
    }
    private val lancamentoDao by lazy {
        AppDatabase.instancia(this).lancamentoDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        tentaCarregarLancamento()
    }

    override fun onResume() {
        super.onResume()
        buscaLancamento()
    }

    private fun buscaLancamento() {
        lifecycleScope.launch {
            lancamentoDao.buscaPorId(lancamentoId).collect { lancamentoCarregado ->
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
                        lancamentoDao.remove(it)
                        finish()
                    }
                }

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
            activityDetalhesLancamentoNome.text = lancamentoCarregado.nome
            activityDetalhesLancamentoTipogasto.text = lancamentoCarregado.tipo
            activityDetalhesLancamentoConta.text = lancamentoCarregado.banco
            activityDetalhesLancamentoValor.text = lancamentoCarregado.valor.toString()
        }
    }

}