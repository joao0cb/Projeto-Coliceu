package com.example.projetocoliceu.data.repository

import com.example.projetocoliceu.data.api.RetrofitClient
import com.example.projetocoliceu.data.model.User
import retrofit2.Response

class CadastroRepository {
    private val api =  RetrofitClient.cadastroApi
    suspend fun cadastrarUsuario(usuario: User): Response<User> {
        return api.cadastrarUsuario(usuario)
    }

}