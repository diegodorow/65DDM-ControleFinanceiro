package br.udesc.ddm.controlefinanceiro.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.udesc.ddm.controlefinanceiro.model.TipoLancamento
import kotlinx.coroutines.flow.Flow

@Dao
interface TipoLancamentoDao {

    @Query("SELECT * FROM TipoLancamento")
    fun buscaTodos(): List<TipoLancamento>

    @Query("SELECT nome FROM TipoLancamento")
    fun buscaTodosSpinner(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salva(vararg tipoLancamento: TipoLancamento)

    @Delete
    fun remove(tipoLancamento: TipoLancamento)

    @Query("select * from TipoLancamento where id = :id")
    fun buscaPorId(id: Long): Flow<TipoLancamento?>
}