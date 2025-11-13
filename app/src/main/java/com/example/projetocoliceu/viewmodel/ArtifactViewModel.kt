package com.example.projetocoliceu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetocoliceu.data.model.Artefato
import com.example.projetocoliceu.data.repository.ArtefatoRepository
import kotlinx.coroutines.launch

// Este ViewModel gerencia o formulário de cadastro de um novo artefato.
class ArtifactViewModel(private val repository: ArtefatoRepository) : ViewModel() {

    // LiveData para notificar a View quando o Artefato for salvo com sucesso.
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    // LiveData para armazenar as coordenadas iniciais passadas pelo mapa.
    private val _initialQuadra = MutableLiveData<String>()
    val initialQuadra: LiveData<String> = _initialQuadra

    private val _initialXRelativo = MutableLiveData<Float>()
    private val _initialYRelativo = MutableLiveData<Float>()

    // LiveData para gerenciar o estado de carregamento/erro
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Campos do formulário que serão ligados aos EditTexts
    val tipo = MutableLiveData<String>()
    val profundidade = MutableLiveData<String>() // Armazena como String para o EditText
    val area = MutableLiveData<String>() // Campo para a área/identificador específico

    // 1. Função chamada pelo Fragmento para injetar as coordenadas iniciais
    fun setInitialCoordinates(quadra: String, xRelativo: Float, yRelativo: Float) {
        _initialQuadra.value = quadra
        _initialXRelativo.value = xRelativo
        _initialYRelativo.value = yRelativo

        // Exemplo: Preencher o campo Área com a Quadra inicialmente (pode ser alterado)
        area.value = quadra
    }

    // 2. Função principal para salvar o novo Artefato
    fun saveNewArtefato() {
        // Validação básica
        if (tipo.value.isNullOrBlank() || profundidade.value.isNullOrBlank() || area.value.isNullOrBlank()) {
            // Poderia emitir um evento de erro aqui
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            // Cria a instância do Artefato a partir dos dados do formulário e das coordenadas
            val newArtefato = Artefato(
                // O ID será gerado pelo backend (0L ou null é um placeholder)
                id = 0L,
                tipo = tipo.value!!,
                profundidade = profundidade.value!!.toFloatOrNull() ?: 0f,
                quadra = _initialQuadra.value!!,
                xRelativo = _initialXRelativo.value!!,
                yRelativo = _initialYRelativo.value!!,
                area = area.value!!
            )

            try {
                // Chama o Repositório para fazer a requisição POST
                val savedArtefato = repository.createArtifact(newArtefato)
                // Se chegou até aqui, foi um sucesso!
                _saveSuccess.value = true
            } catch (e: Exception) {
                // Tratar erro (ex: falha de conexão)
                _saveSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}