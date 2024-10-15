package br.udesc.ddm.controlefinanceiro.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.udesc.ddm.controlefinanceiro.database.converters.Converters
import br.udesc.ddm.controlefinanceiro.database.dao.ContaDao
import br.udesc.ddm.controlefinanceiro.database.dao.LancamentoDao
import br.udesc.ddm.controlefinanceiro.database.dao.TipoLancamentoDao
import br.udesc.ddm.controlefinanceiro.model.Conta
import br.udesc.ddm.controlefinanceiro.model.Lancamento
import br.udesc.ddm.controlefinanceiro.model.TipoLancamento

@Database(
    entities = [
        Conta::class,
        TipoLancamento::class,
        Lancamento::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contaDao(): ContaDao

    abstract fun tipoLancamentoDao(): TipoLancamentoDao

    abstract fun lancamentoDao(): LancamentoDao

    companion object {
        @Volatile
        private var db: AppDatabase? = null
        fun instancia(context: Context): AppDatabase {
            return db ?: Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "controlefinanceiro.db"
            ).allowMainThreadQueries().build().also {
                db = it
            }
        }
    }
}