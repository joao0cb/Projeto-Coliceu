package com.example.projetocoliceu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData // Essencial para converter Flow -> LiveData
import com.example.projetocoliceu.data.model.Artefato
import com.example.projetocoliceu.data.repository.ArtefatoRepository

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
        val novoCartao = Artefato(
            id = java.util.UUID.randomUUID().toString(),
            quadra = nomeQuadra,
            xRelativo = xRelativo,
            yRelativo = yRelativo,
            // Preencha os outros campos com valores padrão ou nulos
            area = "", nivel = "", material = "", data = "", pesquisador = "", obs = null
        )

        // Dispara o evento de navegação para a Activity
        _navigationEvent.value = novoCartao
    }
}


