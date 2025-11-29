package com.example.projetocoliceu.data.repository

import com.example.projetocoliceu.data.db.UserDao
import com.example.projetocoliceu.data.db.toDomainModel
import com.example.projetocoliceu.data.model.User
import com.example.projetocoliceu.data.model.toEntityModel

// Repositório de Usuários (Substitui o CadastroRepository baseado em API)
class UserRepository(private val userDao: UserDao) {

    /**
     * Cadastra um novo usuário localmente no Room.
     * @return O objeto User com o ID gerado pelo Room.
     * @throws Exception se o email já estiver cadastrado.
     */
    suspend fun cadastrarUsuario(usuario: User): User {
        // Verificar se o usuário já existe pelo email
        val existingUser = userDao.getUserByEmail(usuario.email)
        if (existingUser != null) {
            throw Exception("O email ${usuario.email} já está cadastrado.")
        }

        // Converte para Entidade e insere
        val userEntity = usuario.toEntityModel()
        // O insert retorna o novo ID gerado
        val newId = userDao.insert(userEntity).toInt()

        // Retorna o modelo de domínio atualizado com o novo ID
        return userEntity.copy(id = newId).toDomainModel()
    }

    /**
     * Tenta logar um usuário localmente.
     */
    suspend fun login(email: String, senha: String): User {
        val user = userDao.getUserByEmail(email)

        if (user == null || user.senha != senha) {
            throw Exception("Email ou senha inválidos.")
        }
        return user.toDomainModel()
    }
}