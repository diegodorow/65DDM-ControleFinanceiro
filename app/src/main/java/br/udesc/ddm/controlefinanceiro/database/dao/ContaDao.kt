package br.udesc.ddm.controlefinanceiro.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.udesc.ddm.controlefinanceiro.model.Conta
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface ContaDao {
    @Query("SELECT * FROM Conta")
    fun buscaTodas(): Flow<List<Conta>>

    @Query("SELECT nome FROM Conta")
    fun buscaTodosSpinner(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salva(vararg conta: Conta)

    @Delete
    fun remove(conta: Conta)

    @Query("select * from Conta where id = :id")
    fun buscaPorId(id: Long): Flow<Conta?>

    @Query("select coalesce(sum(\n" +
            "\t\tcase when tipolancamento.acao = 'Entrada'\n" +
            "\t\t\t then lancamento.valor\n" +
            "\t\t\t else lancamento.valor * (-1)\n" +
            "\t\t end),0) as valor\n" +
            "  from lancamento \n" +
            "  join conta on (conta.nome = lancamento.banco)\n" +
            "  join tipolancamento on (tipolancamento.nome = lancamento.tipo)\n" +
            " where conta.id = :id")
    fun buscaSaldo(id: Long): BigDecimal
}