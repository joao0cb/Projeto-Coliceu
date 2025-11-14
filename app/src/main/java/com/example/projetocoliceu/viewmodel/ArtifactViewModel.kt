package com.example.projetocoliceu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetocoliceu.data.model.Artefato
import com.example.projetocoliceu.data.repository.ArtefatoRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID // Import necessário para gerar o ID

// O ViewModel gerencia o formulário de cadastro de um novo artefato.
class ArtifactViewModel(private val repository: ArtefatoRepository) : ViewModel() {

    // LiveData para notificar a View quando o Artefato for salvo com sucesso.
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    // LiveData para armazenar as coordenadas iniciais passadas pelo mapa.
    private val _initialQuadra = MutableLiveData<String>()
    val initialQuadra: LiveData<String> = _initialQuadra

    private val _initialSondagem = MutableLiveData<String>()
    private val _initialXRelativo = MutableLiveData<Float>()
    private val _initialYRelativo = MutableLiveData<Float>()

    // LiveData para gerenciar o estado de carregamento/erro
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Campos do formulário que serão ligados aos EditTexts
    // NOTA: 'tipo' aqui mapeia para 'material' na classe Artefato
    val tipo = MutableLiveData<String>()
    val profundidade = MutableLiveData<String>()
    val area = MutableLiveData<String>()
    val pesquisador = MutableLiveData<String>("Pesquisador Padrão") // Adicionado default para não dar erro

    val fotoCaminho = MutableLiveData<String>()

    // 1. Função chamada pelo Fragmento para injetar as coordenadas iniciais
    fun setInitialCoordinates(quadra: String, xRelativo: Float, yRelativo: Float) {
        _initialQuadra.value = quadra
        _initialXRelativo.value = xRelativo
        _initialYRelativo.value = yRelativo
        area.value = quadra // Define a área inicial como o nome da quadra
    }

    // 2. Função principal para salvar o novo Artefato (AGORA CORRETA E ÚNICA)
    fun saveNewArtefato() {
        // Validação: Verifique os campos essenciais
        if (tipo.value.isNullOrBlank() || profundidade.value.isNullOrBlank() || area.value.isNullOrBlank()) {
            // Emite um evento de erro
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            val timestamp = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dataRegistroFormatada = dateFormat.format(Date(timestamp))

            // Cria a instância do Artefato usando TODOS OS PARÂMETROS
            val newArtefato = Artefato(
                // --- CAMPOS ÚNICOS E COORDENADAS ---
                idCartao = UUID.randomUUID().toString(),
                quadra = _initialQuadra.value!!,
                sondagem = _initialSondagem.value!!,
                pontoGPS = null,

                // --- CAMPOS DO FORMULÁRIO ---
                area = area.value!!,
                material = tipo.value!!, // Mapeia 'tipo' do LiveData para 'material' do Artefato

                // --- CAMPOS COM VALORES FIXOS OU SIMPLES DEFAULT ---
                nivel = "01",
                camada = "I",
                decapagem = null,

                data = dataRegistroFormatada,
                pesquisador = pesquisador.value!!,
                obs = null,

                // --- CAMPOS OPCIONAIS ---
                xRelativo = _initialXRelativo.value!!,
                yRelativo = _initialYRelativo.value!!,
                fotoCaminho = null
            )

            try {
                // Chama a função correta do Repositório
                repository.saveArtifact(newArtefato)
                _saveSuccess.value = true
            } catch (e: Exception) {
                // Tratar erro (ex: falha de conexão)
                _saveSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun ArtefatoRepository.saveArtifact(
        newArtefato: Artefato
    ) {
    }
}