package br.udesc.ddm.controlefinanceiro.retrofit

import br.udesc.ddm.controlefinanceiro.model.NovaRespostaApi
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    //    @GET("quote/list")
    @GET("available?search=TR&token=eJGEyu8vVHctULdVdHYzQd")
    fun pesquisarAcao(
    ): Call<NovaRespostaApi>

}