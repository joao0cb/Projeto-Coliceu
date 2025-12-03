package com.example.projetocoliceu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetocoliceu.data.repository.UserRepository
import kotlinx.coroutines.launch

// Usa o mesmo UserRepository injetado via construtor
class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginSucesso = MutableLiveData<Boolean>()
    val loginSucesso: LiveData<Boolean> = _loginSucesso

    private val _mensagemErro = MutableLiveData<String>()
    val mensagemErro: LiveData<String> = _mensagemErro

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fazerLogin(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _mensagemErro.value = "Preencha o email e a senha."
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Chama a função de login no Room. Lança exceção se falhar.
                repository.login(email, senha)

                _loginSucesso.value = true

            } catch (e: Exception) {
                // Captura a exceção do Repositório ("Email ou senha inválidos")
                _mensagemErro.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}