package br.udesc.ddm.controlefinanceiro.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.database.dao.LancamentoDao
import br.udesc.ddm.controlefinanceiro.model.Lancamento
import kotlinx.coroutines.launch

class LancamentoViewModel(application: Application) : AndroidViewModel(application) {

    private val lancamentoDao: LancamentoDao = AppDatabase.instancia(application).lancamentoDao()

    private val _lancamentos = MutableLiveData<List<Lancamento>>()

    val lancamentos: LiveData<List<Lancamento>> = _lancamentos

    init {
        listarTodosLancamentos()
    }

    fun listarTodosLancamentos() {
        viewModelScope.launch {
            _lancamentos.value = lancamentoDao.buscaTodos()
        }
    }

    fun cadastrarLancamento(lancamentoNovo: Lancamento) {
        viewModelScope.launch {
            lancamentoDao.salva(lancamentoNovo)
        }
    }
}