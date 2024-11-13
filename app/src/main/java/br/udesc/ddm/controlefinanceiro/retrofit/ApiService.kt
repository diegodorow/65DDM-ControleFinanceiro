package br.udesc.ddm.controlefinanceiro.retrofit

import br.udesc.ddm.controlefinanceiro.model.RespostaAPI
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {


    //    @GET("quote/list")
//    @GET("available?search=TR&token=eJGEyu8vVHctULdVdHYzQd")
    @GET("quote/{nome}")
    fun pesquisarAcao(@Path("nome") nome: String): Call<RespostaAPI>

}