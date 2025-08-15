package com.example.segura_control

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun webService(baseUrl: String): WebService {
        val gson = GsonBuilder()
            .setLenient() // Permite aceptar JSON no perfectamente formateado
            .create()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(WebService::class.java)
    }
}
