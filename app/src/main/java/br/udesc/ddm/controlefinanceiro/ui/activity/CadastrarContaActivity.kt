package br.udesc.ddm.controlefinanceiro.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.database.dao.ContaDao
import br.udesc.ddm.controlefinanceiro.databinding.ActivityCadastrarContaBinding
import br.udesc.ddm.controlefinanceiro.model.Conta
import kotlinx.coroutines.launch

class CadastrarContaActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastrarContaBinding.inflate(layoutInflater)
    }

    private val contaDao: ContaDao by lazy {
        val db = AppDatabase.instancia(this)
        db.contaDao()
    }

    private var contaId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Cadastrar conta"
        configuraBotaoSalvar()
        tentaCarregarConta()
    }

    override fun onResume() {
        super.onResume()
        tentaBuscarConta()
    }

    private fun configuraBotaoSalvar() {
        val botaoSalvar = binding.activityFormularioContaBotaoSalvar

        botaoSalvar.setOnClickListener {
            lifecycleScope.launch {
                val contaNova = criaConta()
                contaDao.salva(contaNova)
                finish()
            }
        }
    }

    private fun criaConta(): Conta {
        val campoNome = binding.activityCadastroContaNome
        val nome = campoNome.text.toString()
        val urlImagem = vinculaImagem(nome)

        Log.i("CadastrarContaActivity", "criaConta $nome")
        return Conta(
            id = contaId,
            nome = nome,
            imagem = urlImagem
        )
    }

    private fun tentaCarregarConta() {
        contaId = intent.getLongExtra(CHAVE_CONTA_ID, 0L)
    }

    private fun tentaBuscarConta() {
        lifecycleScope.launch {
            contaDao.buscaPorId(contaId).collect {
                it?.let { contaEncontrada ->
                    title = "Alterar conta"
                    preencheCampos(contaEncontrada)
                }
            }
        }
    }

    private fun preencheCampos(conta: Conta) {
        binding.activityCadastroContaNome
            .setText(conta.nome)
    }

    private fun vinculaImagem(conta: String): String {
        var urlImagem:String = IMAGEM_PADRAO

        if (conta.contains("Bradesco")) {
            urlImagem = IMAGEM_BRADESCO
        } else if (conta.contains("Nubank")) {
            urlImagem = IMAGEM_NUBANK
        } else if (conta.contains("Caixa")) {
            urlImagem = IMAGEM_CAIXA_ECONOMICA
        } else if (conta.contains("Brasil")) {
            urlImagem = IMAGEM_BANCO_DO_BRASIL
        } else if (conta.contains("Alelo")) {
            urlImagem = IMAGEM_MEU_ALELO
        } else if (conta.contains("Dinheiro")) {
            urlImagem = IMAGEM_DINHEIRO
        }
        return urlImagem

    }
}