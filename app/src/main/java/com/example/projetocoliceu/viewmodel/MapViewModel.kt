package com.example.projetocoliceu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetocoliceu.data.model.Artefato
import com.example.projetocoliceu.data.repository.ArtefatoRepository // Usando o nome correto: ArtefatoRepository
import kotlinx.coroutines.launch

// O ViewModel recebe o Repositório como dependência.
class MapViewModel(private val repository: ArtefatoRepository) : ViewModel() {

    // LiveData: Um observável que a UI (Activity/View) irá observar.
    // Ele contém a lista de artefatos que o mapa precisa desenhar.
    private val _artefatos = MutableLiveData<List<Artefato>>()
    val artefatos: LiveData<List<Artefato>> = _artefatos

    // LiveData para notificar a UI de que está carregando ou houve um erro.
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Chamado quando o ViewModel é inicializado ou quando o mapa precisa de dados.
    init {
        loadArtefatos()
    }

    // Função para buscar os artefatos do servidor Spring Boot
    fun loadArtefatos() {
        // viewModelScope: Coroutine Scope vinculado ao ciclo de vida do ViewModel
        viewModelScope.launch {
            _isLoading.value = true

            // 1. Chamar o Repositório (que vai buscar no Spring Boot)
            val result = repository.getAllArtifacts()

            // 2. Atualizar o estado (LiveData)
            _artefatos.value = result
            _isLoading.value = false

            // O código do CustomView irá observar _artefatos para desenhar os marcadores.
        }
    }

    /**
     * Função para preparar a criação de um novo Artefato
     * @param nomeQuadra O nome da quadra onde o toque ocorreu
     * @param xRelativo A posição X dentro da quadra (0.0 a 1.0)
     * @param yRelativo A posição Y dentro da quadra (0.0 a 1.0)
     */
    fun startNewArtefato(nomeQuadra: String, xRelativo: Float, yRelativo: Float) {
        // Esta função deve iniciar o fragmento de formulário e passar as coordenadas.
        // O código de navegação virá aqui depois!
        println("Novo Artefato na Quadra: $nomeQuadra, X: $xRelativo, Y: $yRelativo")
    }
}