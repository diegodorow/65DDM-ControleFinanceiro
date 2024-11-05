package br.udesc.ddm.controlefinanceiro.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class Lancamento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nome: String,
    val banco: String,
    val tipo: String,
    val valor: BigDecimal,
    val diretorioImagem: String? = null
)