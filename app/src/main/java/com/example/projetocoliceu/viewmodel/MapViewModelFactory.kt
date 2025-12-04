package com.example.projetocoliceu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projetocoliceu.data.model.Artefato
import com.example.projetocoliceu.data.repository.ArtefatoRepository
import com.example.projetocoliceu.data.repository.MapRepository

/**
 * Factory que permite ao Android criar o MapViewModel,
 * passando a instância do ArtefatoRepository para o seu construtor.
 */
class MapViewModelFactory(private val repositoryArtefato: ArtefatoRepository, private val repositoryMap: MapRepository) : ViewModelProvider.Factory {

    // Sobrescreve a função que o sistema Android chama para criar o ViewModel.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica se a classe que o sistema quer criar é o MapViewModel.
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            // Se for, cria uma nova instância do MapViewModel e passa o Repositório.
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(repositoryArtefato, repositoryMap ) as T
        }
        // Se for outra classe, lança uma exceção.
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}