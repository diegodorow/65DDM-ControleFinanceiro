package br.udesc.ddm.controlefinanceiro.ui.listacontas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListaContasViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is LISTACONTA"
    }
    val text: LiveData<String> = _text
}