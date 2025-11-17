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

    // Variáveis externas (públicas e imutáveis)
    val initialXRelativo: LiveData<Float>
        get() = _initialXRelativo // Expondo o valor para leitura

    val initialYRelativo: LiveData<Float>
        get() = _initialYRelativo // Expondo o valor para leitura

    // --- ESTADO DA VIEW E FEEDBACK ---

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // --- CAMPOS DO FORMULÁRIO (LiveData BINDING) ---
    val obs = MutableLiveData<String>()

    val tipo = MutableLiveData<String>() // Mapeia para 'material'
    val area = MutableLiveData<String>()
    val pesquisador = MutableLiveData<String>("Pesquisador Padrão")
    val fotoCaminho = MutableLiveData<String>()

    // --- FUNÇÕES DE INJEÇÃO E PREPARAÇÃO ---

    // 1. Injeta as coordenadas iniciais do Mapa
    fun setInitialCoordinates(quadra: String, xRelativo: Float, yRelativo: Float, sondagem: String = "N/A") {
        _initialQuadra.value = quadra
        _initialXRelativo.value = xRelativo
        _initialYRelativo.value = yRelativo
        _initialSondagem.value = sondagem
        area.value = quadra
    }

    // 2. Injeta um Artefato existente para edição
    fun setArtifactToEdition(artefato: Artefato) {
        _artefatoEditavel.value = artefato
        // Preenche os LiveDatas do formulário com os dados do artefato existente
        area.value = artefato.area
        tipo.value = artefato.material // material
        pesquisador.value = artefato.pesquisador
        // ... (preencher todos os campos do formulário)
    }

    // --- FUNÇÃO PRINCIPAL DE SALVAR/ATUALIZAR (CRUD: C e U) ---

    fun saveOrUpdateArtifact() {
        // Validação: Verifique os campos essenciais
        if (tipo.value.isNullOrBlank() || area.value.isNullOrBlank()) {
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
                    data = artefatoExistente.data,
                    obs = obs.value
                    // Supondo que obs também é um campo do formulário

                )

            } else {
                // --- MODO CRIAÇÃO (CREATE) ---
                artefatoFinal = Artefato(
                    // Geração de IDs e Coordenadas
                    id = UUID.randomUUID().toString(),
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
                    obs = obs.value,
                    fotoCaminho = fotoCaminho.value
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

}