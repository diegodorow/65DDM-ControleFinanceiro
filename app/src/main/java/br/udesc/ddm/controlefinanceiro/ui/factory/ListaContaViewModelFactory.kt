package br.udesc.ddm.controlefinanceiro.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.udesc.ddm.controlefinanceiro.database.repository.ContasRepository
import br.udesc.ddm.controlefinanceiro.ui.listacontas.ListaContasViewModel

class ListaContaViewModelFactory {
    class RegisterViewModelFactory(private val contasRepository: ContasRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ListaContasViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ListaContasViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}