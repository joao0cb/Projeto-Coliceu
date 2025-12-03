package com.example.projetocoliceu.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UserEntity(
    // ID gerado pelo Room
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val email: String,
    val senha: String
)

// Mapeamento DB -> Domain
fun UserEntity.toDomainModel(): com.example.projetocoliceu.data.model.User {
    return com.example.projetocoliceu.data.model.User(
        id = this.id,
        nome = this.nome,
        email = this.email,
        senha = this.senha
    )
}