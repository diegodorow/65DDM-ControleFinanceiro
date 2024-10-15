package br.udesc.ddm.controlefinanceiro.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.database.dao.ContaDao
import br.udesc.ddm.controlefinanceiro.database.dao.LancamentoDao
import br.udesc.ddm.controlefinanceiro.database.dao.TipoLancamentoDao
import br.udesc.ddm.controlefinanceiro.databinding.ActivityCadastrarLancamentoBinding
import br.udesc.ddm.controlefinanceiro.model.Lancamento
import kotlinx.coroutines.launch
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

    private var lancamentoId = 0L
    val listaConta = contaDao.buscaTodosSpinner()
    val listaTiposGasto = tipoLancamentoDao.buscaTodosSpinner()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Cadastrar lançamento"
        configuraSpinner()
        configuraBotaoSalvar()
        tentaCarregarLancamento()
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

        return Lancamento(
            id = lancamentoId,
            nome = nome,
            tipo = tipoGasto,
            banco = conta,
            valor = valor
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
}