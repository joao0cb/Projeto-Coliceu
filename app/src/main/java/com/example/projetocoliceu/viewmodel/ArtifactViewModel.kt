package com.example.projetocoliceu.viewmodel

import android.util.Log
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
    private val _artefatoEditavel = MutableLiveData<Artefato?>()
    val artefatoEditavel: LiveData<Artefato?> = _artefatoEditavel

    private val _initialQuadra = MutableLiveData<String>()
    val initialQuadra: LiveData<String> = _initialQuadra

    private val _initialSondagem = MutableLiveData<String>()
    val initialSondagem: LiveData<String> = _initialSondagem // Exposto para ser usado no Fragment

    private val _initialXRelativo = MutableLiveData<Float>()
    val initialXRelativo: LiveData<Float>
        get() = _initialXRelativo

    private val _initialYRelativo = MutableLiveData<Float>()
    val initialYRelativo: LiveData<Float>
        get() = _initialYRelativo

    // --- ESTADO DA VIEW E FEEDBACK ---
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _artefatosUpdated = MutableLiveData<Boolean>()
    val artefatosUpdated: LiveData<Boolean> = _artefatosUpdated

    private val _artifact = MutableLiveData<Artefato>()
    val artifact: LiveData<Artefato> get() = _artifact


    // ----------------------------------------------------------------------
    // --- CAMPOS DO FORMULÁRIO (LiveData BINDING) ---
    // ----------------------------------------------------------------------

    // Coordenadas/Localização
    val area = MutableLiveData<String>() // Obrigatório (ex: "C5")

    val nome = MutableLiveData<String>()
    val quadra = MutableLiveData<String>()
    val sondagem = MutableLiveData<String>() // Inicialmente preenchido pelo mapa
    val pontoGPS = MutableLiveData<String?>() // Opcional

    // Contexto Estratigráfico
    val nivel = MutableLiveData<Int>() // Assume Int
    val camada = MutableLiveData<String>()
    val decapagem = MutableLiveData<String?>() // Opcional

    // Detalhes do Achado
    val material = MutableLiveData<String>() // Mapeia para 'tipo' (Cerâmica, Lítico)
    val quantidade = MutableLiveData<Int>(1) // Default para 1
    val fotoCaminho = MutableLiveData<String?>() // Opcional (caminho local)

    // Logística e Observações
    val pesquisador = MutableLiveData<String?>() // Assume pesquisador padrão será definido na view/injeção
    val data = MutableLiveData<String?>() // Pode ser deixado para preenchimento automático
    val obs = MutableLiveData<String?>() // Opcional

    // --- FUNÇÕES DE INJEÇÃO E PREPARAÇÃO ---

    // 1. Injeta as coordenadas iniciais do Mapa
    fun setInitialCoordinates(quadra: String, xRelativo: Float, yRelativo: Float, sondagem: String = "N/A") {
        _initialQuadra.value = quadra
        _initialXRelativo.value = xRelativo
        _initialYRelativo.value = yRelativo
        _initialSondagem.value = sondagem

        // Preenche os campos do formulário com dados iniciais
        this.quadra.value = quadra
        this.sondagem.value = sondagem

        // Define valores padrão se estiver em modo de criação
        if (_artefatoEditavel.value == null) {
            area.value = ""
            nome.value= ""
            nivel.value = 1
            camada.value = "I"
            material.value = ""        // será preenchido pelo usuário
            quantidade.value = 1
            pesquisador.value = ""     // opcional
            obs.value = ""

            // Preencher data atual
            val hoje = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            data.value = hoje
            // Preencher data atual (ou deixar a função save cuidar disso)
        }
    }
    private val _mapId = MutableLiveData<String>()
    val mapId: LiveData<String> get() = _mapId

    fun setMapId(id: String) {
        _mapId.value = id
    }

    fun saveArtifact() {
        val art = Artefato(
            area = area.value ?: "",
            nome = nome.value ?: "",
            mapId = _mapId.value ?: return,
            quadra = quadra.value ?: "",
            sondagem = sondagem.value ?: "",
            pontoGPS = pontoGPS.value,
            nivel = nivel.value?.toString() ?: "1",  // converte Int para String
            camada = camada.value ?: "",
            decapagem = decapagem.value,
            material = material.value ?: "",
            quantidade = quantidade.value ?: 1,
            data = data.value ?: SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            pesquisador = pesquisador.value ?: "",
            obs = obs.value ?: "",
            xRelativo = _initialXRelativo.value ?: 0f,
            yRelativo = _initialYRelativo.value ?: 0f,
            fotoCaminho = fotoCaminho.value
        )

        viewModelScope.launch {
            repository.saveArtifact(art)
            _saveSuccess.postValue(true) // dispara o observer no fragment

            _artefatosUpdated.postValue(true)
        }
    }

    // 2. Injeta um Artefato existente para edição
    fun setArtifactToEdition(artefato: Artefato?) {
        _artefatoEditavel.value = artefato
        if (artefato == null) {
            // Limpar campos para modo criação (mantém valores de initialQuadra/x/y)
            quadra.value = _initialQuadra.value ?: ""
            sondagem.value = _initialSondagem.value ?: ""
            area.value = ""
            nome.value = ""
            pontoGPS.value = null
            nivel.value = 1
            camada.value = "I"
            decapagem.value = null
            material.value = ""
            quantidade.value = 1
            pesquisador.value = null
            data.value = null
            obs.value = null
            fotoCaminho.value = null
        } else {
            // Preenche com dados do artefato (sua implementação já tinha isso; pode reutilizar)
            quadra.value = artefato.quadra
            area.value = artefato.area
            nome.value = artefato.nome
            sondagem.value = artefato.sondagem
            pontoGPS.value = artefato.pontoGPS
            nivel.value = artefato.nivel.toIntOrNull()
            camada.value = artefato.camada
            decapagem.value = artefato.decapagem
            material.value = artefato.material
            quantidade.value = artefato.quantidade
            pesquisador.value = artefato.pesquisador
            data.value = artefato.data
            obs.value = artefato.obs
            fotoCaminho.value = artefato.fotoCaminho

        }
    }

    fun clearEditionIfNeeded() {
        _artefatoEditavel.value = null
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
    // --- FUNÇÃO PRINCIPAL DE SALVAR/ATUALIZAR (CRUD: C e U) ---
    // --- LISTA DE ARTEFATOS E CARREGAMENTO ---
    private val _artifacts = MutableLiveData<List<Artefato>>()
    val artifacts: LiveData<List<Artefato>> = _artifacts

    fun fetchArtifacts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Supondo que o Repositório tenha uma função para obter todos os artefatos
                _artifacts.value = repository.getAllArtifacts() as List<Artefato>?
            } catch (e: Exception) {
                // Lógica de tratamento de erro
            } finally {
                _isLoading.value = false
            }
        }
    }
    // --- FUNÇÃO PARA DELETAR (CRUD: D) ---
    fun deleteArtifact(artefato: Artefato) {
        viewModelScope.launch {
            try {
                repository.deleteArtifact(artefato)
                fetchArtifacts()
                _deleteSuccess.postValue(true) // aqui dispara o observer no fragment
            } catch (e: Exception) {
                _deleteSuccess.postValue(false) // opcional, para indicar falha
            }
        }
    }



}