package br.udesc.ddm.controlefinanceiro.retrofit

import br.udesc.ddm.controlefinanceiro.model.RespostaAPI
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

//    @GET("pesquisar")
//    suspend fun pesquisarMedicamentos(
//        @Query("nome") nome: String,
//        @Query("pagina") pagina: Int
//    ): ApiResponse

//    @GET("quote/list")
    @GET("available?search=TR&token=eJGEyu8vVHctULdVdHYzQd")
    fun pesquisarAcao(
    ): Call<RespostaAPI>

}