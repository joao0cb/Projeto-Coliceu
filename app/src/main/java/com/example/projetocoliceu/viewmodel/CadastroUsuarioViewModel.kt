package com.example.projetocoliceu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetocoliceu.data.model.User
import com.example.projetocoliceu.data.repository.CadastroRepository
import kotlinx.coroutines.launch

class CadastroUsuarioViewModel : ViewModel() {

    private val repository = CadastroRepository()

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
                val response = repository.cadastrarUsuario(novoUsuario)
                if (response.isSuccessful) {
                    _cadastroSucesso.value = true
                } else {
                    _mensagemErro.value = "Erro: ${response.code()} - Tente outro email."
                }
            } catch (e: Exception) {
                _mensagemErro.value = "Falha na conexão: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}