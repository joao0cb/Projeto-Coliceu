package com.example.projetocoliceu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projetocoliceu.data.repository.ArtefatoRepository

class ArtifactViewModelFactory ( private val repository: ArtefatoRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ArtifactViewModel::class.java)) {
                return ArtifactViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }