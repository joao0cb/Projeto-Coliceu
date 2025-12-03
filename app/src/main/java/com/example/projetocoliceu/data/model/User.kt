package com.example.projetocoliceu.data.model

import com.example.projetocoliceu.data.db.UserEntity

data class User(
    val id: Int? = null,
    val nome: String,
    val email: String,
    val senha: String
)

// Mapeamento Domain -> DB
fun User.toEntityModel(): UserEntity {
    // Se o ID for null (novo cadastro), o Room irá gerar um
    return UserEntity(
        id = this.id ?: 0, // 0 é o valor padrão para autoGenerate=true
        nome = this.nome,
        email = this.email,
        senha = this.senha
    )
}