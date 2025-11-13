package com.example.projetocoliceu.data.api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // IMPORTANTE: Mude esta URL para o endereço do seu servidor Spring Boot.
    // 'http://10.0.2.2' é um alias especial para o 'localhost' (127.0.0.1) quando rodando no emulador Android.
    private const val BASE_URL = "http://10.0.2.2:8080/"
    // Se você estiver usando um dispositivo físico, você deve usar o IP da sua máquina na rede local (Ex: http://192.168.1.10:8080/)

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Adiciona o conversor que lida com JSON (Gson)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Função que cria e retorna a instância do nosso serviço de API
    val apiService: ArtifactApiService by lazy {
        retrofit.create(ArtifactApiService::class.java)
    }
}