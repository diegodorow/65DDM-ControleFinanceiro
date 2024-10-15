package br.udesc.ddm.controlefinanceiro.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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
}