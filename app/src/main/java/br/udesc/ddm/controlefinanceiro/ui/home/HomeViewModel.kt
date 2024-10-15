package br.udesc.ddm.controlefinanceiro.ui.home

import androidx.lifecycle.ViewModel
import br.udesc.ddm.controlefinanceiro.database.repository.ContasRepository

class HomeViewModel() : ViewModel() {

    private val repository: ContasRepository? = null

    fun buscaTodasContas() = repository?.listarTodasContas()

}