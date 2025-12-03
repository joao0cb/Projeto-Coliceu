package com.example.projetocoliceu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projetocoliceu.data.repository.UserRepository

// A Factory deve ser capaz de criar AMBOS os ViewModels
class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // 1. Lógica para CadastroUsuarioViewModel
        if (modelClass.isAssignableFrom(CadastroUsuarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CadastroUsuarioViewModel(repository) as T
        }

        // 2. Lógica para LoginViewModel (ADICIONADA)
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T // <--- AGORA CRIA O VIEWMODEL DE LOGIN
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}