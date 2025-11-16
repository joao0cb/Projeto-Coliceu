package com.example.projetocoliceu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData // Essencial para converter Flow -> LiveData
import com.example.projetocoliceu.data.model.Artefato
import com.example.projetocoliceu.data.repository.ArtefatoRepository
import java.util.UUID

class MapViewModel(private val repository: ArtefatoRepository) : ViewModel() {
    val artefatos: LiveData<List<Artefato>> =
        repository.getAllArtifacts().asLiveData() // Retorna Flow<List<ArtefatoEntity>>

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _navigationEvent = MutableLiveData<Artefato>()
    val navigationEvent: LiveData<Artefato> = _navigationEvent

    /**
     * Função para preparar a criação de um novo Artefato
     */
    fun startNewArtefato(nomeQuadra: String, xRelativo: Float, yRelativo: Float) {
        // Exemplo de criação do objeto inicial
        val novo = Artefato(
            id = UUID.randomUUID().toString(),
            quadra = nomeQuadra,
            area = "",
            sondagem = "",
            pontoGPS = null,
            nivel = "",
            camada = "",
            decapagem = null,
            material = "",
            quantidade = 1,
            data = "",
            pesquisador = "",
            obs = null,
            xRelativo = xRelativo,
            yRelativo = yRelativo,
            fotoCaminho = null
        )

        // Dispara o evento de navegação para a Activity
        _navigationEvent.value = novo
    }
}


