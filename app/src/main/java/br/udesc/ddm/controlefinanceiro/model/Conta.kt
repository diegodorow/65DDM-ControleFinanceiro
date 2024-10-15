package br.udesc.ddm.controlefinanceiro.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Conta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nome: String,
    val imagem: String? = null
)