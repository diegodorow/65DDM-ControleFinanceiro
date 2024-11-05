package br.udesc.ddm.controlefinanceiro.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

class RetrofitInitializer {

    val token = "Bearer d8pa2h1pwVhC41Cv8sNiYS"
    val client = OkHttpClient.Builder()
//        .addInterceptor(token)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://brapi.dev/api/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun create(): ApiService {
        return retrofit.create()
    }
}