package br.udesc.ddm.controlefinanceiro.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.udesc.ddm.controlefinanceiro.model.Lancamento
import kotlinx.coroutines.flow.Flow

@Dao
interface LancamentoDao {
    @Query("SELECT * FROM Lancamento")
    fun buscaTodos(): List<Lancamento>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salva(vararg lancamento: Lancamento)

    @Delete
    fun remove(lancamento: Lancamento)

    @Query("select * from Lancamento where id = :id")
    fun buscaPorId(id: Long): Flow<Lancamento?>
}