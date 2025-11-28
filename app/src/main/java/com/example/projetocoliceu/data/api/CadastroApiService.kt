package com.example.projetocoliceu.data.api

import com.example.projetocoliceu.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface cadastroApiService {
    @POST("usuarios")
    suspend fun cadastrarUsuario(
        @Body usuario: User
    ): Response<User>
}