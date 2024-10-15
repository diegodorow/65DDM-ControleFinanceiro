package br.udesc.ddm.controlefinanceiro.database.repository

import br.udesc.ddm.controlefinanceiro.database.dao.ContaDao
import br.udesc.ddm.controlefinanceiro.model.Conta
import kotlinx.coroutines.flow.Flow

class ContasRepository(private val contaDao: ContaDao) {

    fun listarTodasContas(): Flow<List<Conta>>? {
        try {
            val listaContas: Flow<List<Conta>> = contaDao.buscaTodas()
            return listaContas
        } catch (e: Exception) {
            return null
        }
    }


}