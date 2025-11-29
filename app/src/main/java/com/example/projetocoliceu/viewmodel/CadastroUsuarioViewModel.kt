package com.example.projetocoliceu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.projetocoliceu.data.model.User
import com.example.projetocoliceu.data.repository.UserRepository // Assumindo que este é o novo UserRepository

// O Repositório deve ser injetado via construtor, não instanciado diretamente.
class CadastroUsuarioViewModel(private val repository: UserRepository) : ViewModel() {
    // ^ CORREÇÃO AQUI: Recebendo o Repositório

    // LiveData para observar o resultado na tela
    private val _cadastroSucesso = MutableLiveData<Boolean>()
    val cadastroSucesso: LiveData<Boolean> = _cadastroSucesso

    private val _mensagemErro = MutableLiveData<String>()
    val mensagemErro: LiveData<String> = _mensagemErro

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun cadastrarUsuario(nome: String, email: String, pass: String) {
        // Validação básica
        if (nome.isBlank() || email.isBlank() || pass.isBlank()) {
            _mensagemErro.value = "Preencha todos os campos"
            return
        }

        val novoUsuario = User(nome = nome, email = email, senha = pass)

        _isLoading.value = true

        viewModelScope.launch {
            try {
                // CORREÇÃO: Chamando o método local. Ele lança uma exceção se falhar (ex: email duplicado).
                repository.cadastrarUsuario(novoUsuario)

                // Se o código chegou até aqui, significa que o cadastro foi bem-sucedido no Room.
                _cadastroSucesso.value = true

            } catch (e: Exception) {
                // CORREÇÃO: Capturando a exceção lançada pelo UserRepository (ex: "O email já está cadastrado.")
                // e.message é a mensagem de erro que você configurou no Repositório.
                _mensagemErro.value = "Falha no cadastro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}