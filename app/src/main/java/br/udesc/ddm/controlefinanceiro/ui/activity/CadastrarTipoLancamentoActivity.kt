package br.udesc.ddm.controlefinanceiro.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.database.dao.TipoLancamentoDao
import br.udesc.ddm.controlefinanceiro.databinding.ActivityCadastrarTipoLancamentoBinding
import br.udesc.ddm.controlefinanceiro.model.TipoLancamento
import kotlinx.coroutines.launch

class CadastrarTipoLancamentoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastrarTipoLancamentoBinding.inflate(layoutInflater)
    }

    private val tipoLancamentoDao: TipoLancamentoDao by lazy {
        val db = AppDatabase.instancia(this)
        db.tipoLancamentoDao()
    }

    private var tipoLancamentoId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Cadastrar tipo lançamento"
        configuraSpinner()
        configuraBotaoSalvar()
        tentaCarregarTipoLancamento()
    }

    override fun onResume() {
        super.onResume()
        tentaBuscarTipoLancamento()
    }

    private fun configuraBotaoSalvar() {
        val botaoSalvar = binding.activityFormularioTipolancamentoBotaoSalvar

        botaoSalvar.setOnClickListener {
            lifecycleScope.launch {
                val tipoLancamentoNovo = criaTipoLancamento()
                tipoLancamentoDao.salva(tipoLancamentoNovo)
                finish()
            }
        }
    }

    private fun criaTipoLancamento(): TipoLancamento {
        val campoNome = binding.activityCadastroTipolancamentoNome
        val nome = campoNome.text.toString()
        val acao = binding.spAcao.selectedItem.toString()

        return TipoLancamento(
            id = tipoLancamentoId,
            nome = nome,
            acao = acao
        )
    }

    private fun tentaCarregarTipoLancamento() {
        tipoLancamentoId = intent.getLongExtra(CHAVE_TIPOLANCAMENTO_ID, 0L)
    }

    private fun tentaBuscarTipoLancamento() {
        lifecycleScope.launch {
            tipoLancamentoDao.buscaPorId(tipoLancamentoId).collect {
                it?.let { tipoLancamentoEncontrado ->
                    title = "Alterar tipo lançamento"
                    preencheCampos(tipoLancamentoEncontrado)
                }
            }
        }
    }

    private fun preencheCampos(tipoLancamento: TipoLancamento) {
        binding.activityCadastroTipolancamentoNome
            .setText(tipoLancamento.nome)

        var acao: Int = 0
        if(tipoLancamento.acao == "Saída") {
            acao = 1
        }

        binding.spAcao.setSelection(acao)
    }

    private fun configuraSpinner() {
        val spTipoConta = binding.spAcao
        val listaTiposAcao = arrayOf(TIPO_ENTRADA, TIPO_SAIDA)
        val adapterTipoConta =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaTiposAcao)
        adapterTipoConta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoConta.adapter = adapterTipoConta
    }
}