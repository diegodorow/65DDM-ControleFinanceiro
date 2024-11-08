package br.udesc.ddm.controlefinanceiro.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.database.dao.ContaDao
import br.udesc.ddm.controlefinanceiro.model.Conta
import br.udesc.ddm.controlefinanceiro.ui.activity.IMAGEM_BANCO_DO_BRASIL
import br.udesc.ddm.controlefinanceiro.ui.activity.IMAGEM_BRADESCO
import br.udesc.ddm.controlefinanceiro.ui.activity.IMAGEM_CAIXA_ECONOMICA
import br.udesc.ddm.controlefinanceiro.ui.activity.IMAGEM_DINHEIRO
import br.udesc.ddm.controlefinanceiro.ui.activity.IMAGEM_MEU_ALELO
import br.udesc.ddm.controlefinanceiro.ui.activity.IMAGEM_NUBANK
import br.udesc.ddm.controlefinanceiro.ui.activity.IMAGEM_PADRAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class ContaViewModel(application: Application) : AndroidViewModel(application) {

    private val contaDAO: ContaDao = AppDatabase.instancia(application).contaDao()

    private val _contas = MutableLiveData<List<Conta>>()

    val contas: LiveData<List<Conta>> = _contas

    init {
        listarTodasContas()
    }

    fun listarTodasContas() {
        viewModelScope.launch {
            _contas.value = contaDAO.buscaTodas()
        }
    }

    fun cadastrarConta(nome: String) {
        val imagem = vinculaImagem(nome)
        viewModelScope.launch {
            val conta = Conta(nome = nome, imagem = imagem)
            contaDAO.salva(conta)
            listarTodasContas()
        }
    }

    fun buscaTodosParaSpinner(): List<String> {
        return contaDAO.buscaTodosSpinner()
    }

    fun buscaPorId(contaId: Long): Flow<Conta?> {
        return contaDAO.buscaPorId(contaId)
    }

    fun buscarTodasContas(): List<Conta> {
        return contaDAO.buscaTodas()
    }

    fun remove(conta: Conta) {
        return contaDAO.remove(conta)
    }

    private fun vinculaImagem(conta: String): String {
        var urlImagem: String = IMAGEM_PADRAO

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

    fun getSaldoConta(contaId: Long): BigDecimal {
        return contaDAO.buscaSaldo(contaId)
    }

}