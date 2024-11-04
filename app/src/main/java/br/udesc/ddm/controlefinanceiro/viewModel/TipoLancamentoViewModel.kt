package br.udesc.ddm.controlefinanceiro.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.database.dao.TipoLancamentoDao
import br.udesc.ddm.controlefinanceiro.model.TipoLancamento
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
}