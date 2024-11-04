package br.udesc.ddm.controlefinanceiro.database.repository

import br.udesc.ddm.controlefinanceiro.model.Conta

public interface ContasRepository {

    fun salvaConta(conta: Conta?)
    fun buscaTodasContas(): List<Conta>

}