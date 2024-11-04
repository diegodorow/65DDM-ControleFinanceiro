package br.udesc.ddm.controlefinanceiro.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.udesc.ddm.controlefinanceiro.database.dao.ContaDao
import br.udesc.ddm.controlefinanceiro.database.AppDatabase
import br.udesc.ddm.controlefinanceiro.model.Conta
import kotlinx.coroutines.launch

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

}