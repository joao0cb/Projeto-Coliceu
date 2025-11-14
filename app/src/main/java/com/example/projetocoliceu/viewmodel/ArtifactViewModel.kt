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
import java.util.UUID

class ArtifactViewModel(private val repository: ArtefatoRepository) : ViewModel() {

    // --- ESTADO DE EDIÇÃO E COORDENADAS ---

    // LiveData para armazenar o Artefato que está sendo editado. Se for null, é um novo cadastro.
    private val _artefatoEditavel = MutableLiveData<Artefato?>()
    val artefatoEditavel: LiveData<Artefato?> = _artefatoEditavel

    // LiveData para armazenar as coordenadas iniciais passadas pelo mapa.
    private val _initialQuadra = MutableLiveData<String>()
    val initialQuadra: LiveData<String> = _initialQuadra

    private val _initialSondagem = MutableLiveData<String>()
    private val _initialXRelativo = MutableLiveData<Float>()
    private val _initialYRelativo = MutableLiveData<Float>()

    // --- ESTADO DA VIEW E FEEDBACK ---

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // --- CAMPOS DO FORMULÁRIO (LiveData BINDING) ---

    val tipo = MutableLiveData<String>() // Mapeia para 'material'
    val profundidade = MutableLiveData<String>() // Campo não utilizado no Artefato.kt, mantido para o formulário.
    val area = MutableLiveData<String>()
    val pesquisador = MutableLiveData<String>("Pesquisador Padrão")
    val fotoCaminho = MutableLiveData<String>()

    // --- FUNÇÕES DE INJEÇÃO E PREPARAÇÃO ---

    // 1. Injeta as coordenadas iniciais do Mapa
    fun setInitialCoordinates(quadra: String, xRelativo: Float, yRelativo: Float) {
        _initialQuadra.value = quadra
        _initialXRelativo.value = xRelativo
        _initialYRelativo.value = yRelativo
        area.value = quadra
    }

    // 2. Injeta um Artefato existente para edição
    fun setArtefatoParaEdicao(artefato: Artefato) {
        _artefatoEditavel.value = artefato
        // Preenche os LiveDatas do formulário com os dados do artefato existente
        area.value = artefato.area
        tipo.value = artefato.material // material
        pesquisador.value = artefato.pesquisador
        // ... (preencher todos os campos do formulário)
    }

    // --- FUNÇÃO PRINCIPAL DE SALVAR/ATUALIZAR (CRUD: C e U) ---

    fun saveOrUpdateArtefato() {
        // Validação: Verifique os campos essenciais
        if (tipo.value.isNullOrBlank() || area.value.isNullOrBlank()) {
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            val artefatoFinal: Artefato
            val isUpdating = _artefatoEditavel.value != null

            // 1. Preparação da Data (String Formatada)
            val timestamp = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dataRegistroFormatada = dateFormat.format(Date(timestamp))

            // 2. Criação/Atualização da Instância
            if (isUpdating) {
                // --- MODO ATUALIZAÇÃO (UPDATE) ---
                val artefatoExistente = _artefatoEditavel.value!! // Artefato carregado

                // Cria uma nova instância, mantendo IDs e coordenadas originais
                artefatoFinal = artefatoExistente.copy(
                    // CAMPOS ATUALIZADOS PELO FORMULÁRIO
                    area = area.value!!,
                    material = tipo.value!!,
                    pesquisador = pesquisador.value!!,

                    // CAMPOS DE DATA/LOGÍSTICA QUE SÃO SEMPRE ATUALIZADOS
                    data = dataRegistroFormatada,
                    obs = null // Supondo que obs também é um campo do formulário
                    // ... (aplicar todos os campos que podem ser editados)
                )

            } else {
                // --- MODO CRIAÇÃO (CREATE) ---
                artefatoFinal = Artefato(
                    // Geração de IDs e Coordenadas
                    idCartao = UUID.randomUUID().toString(),
                    quadra = _initialQuadra.value!!,
                    xRelativo = _initialXRelativo.value!!,
                    yRelativo = _initialYRelativo.value!!,
                    sondagem = _initialSondagem.value ?: "N/A", // Valor default seguro
                    pontoGPS = null,

                    // Dados do Formulário
                    area = area.value!!,
                    material = tipo.value!!,
                    pesquisador = pesquisador.value!!,

                    // Dados Fixos/Default
                    nivel = "01",
                    camada = "I",
                    decapagem = null,
                    quantidade = 1,

                    // Logística
                    data = dataRegistroFormatada,
                    obs = null,
                    fotoCaminho = null
                )
            }

            // 3. Chamada ao Repositório
            try {
                if (isUpdating) {
                    repository.updateArtifact(artefatoFinal)
                } else {
                    repository.saveArtifact(artefatoFinal)
                }
                _saveSuccess.value = true
            } catch (e: Exception) {
                _saveSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    // As funções vazias que estavam causando o erro foram removidas.
    // A função 'updateExistingArtefato' foi incorporada à 'saveOrUpdateArtefato'.
}