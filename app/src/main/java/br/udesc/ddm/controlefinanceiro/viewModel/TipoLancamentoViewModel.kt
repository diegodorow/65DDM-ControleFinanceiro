package br.udesc.ddm.controlefinanceiro.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.database.dao.TipoLancamentoDao
import br.udesc.ddm.controlefinanceiro.model.TipoLancamento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TipoLancamentoViewModel(application: Application) : AndroidViewModel(application) {

    private val tipoLancamentoDao: TipoLancamentoDao =
        AppDatabase.instancia(application).tipoLancamentoDao()

    private val _tiposLancamentos = MutableLiveData<List<TipoLancamento>>()

    val tipos: LiveData<List<TipoLancamento>> = _tiposLancamentos

    init {
        listarTodosTiposLancamentos()
    }

    fun listarTodosTiposLancamentos() {
        viewModelScope.launch {
            _tiposLancamentos.value = tipoLancamentoDao.buscaTodos()
        }
    }

    fun cadastrarTipoLancamento(tipoLancamentoNovo: TipoLancamento) {
        viewModelScope.launch {
            tipoLancamentoDao.salva(tipoLancamentoNovo)
        }
    }

    fun buscaTodosParaSpinner(): List<String> {
        return tipoLancamentoDao.buscaTodosSpinner()
    }

    fun buscaPorId(tipoLancamentoId: Long): Flow<TipoLancamento?> {
        return tipoLancamentoDao.buscaPorId(tipoLancamentoId)
    }

    fun buscarTodosTipos(): List<TipoLancamento> {
        return tipoLancamentoDao.buscaTodos()
    }

    fun remove(tipoLancamento: TipoLancamento) {
        return tipoLancamentoDao.remove(tipoLancamento)
    }
}