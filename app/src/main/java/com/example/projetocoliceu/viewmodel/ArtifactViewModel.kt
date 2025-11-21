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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // ----------------------------------------------------------------------
    // --- CAMPOS DO FORMULÁRIO (LiveData BINDING) ---
    // ----------------------------------------------------------------------

    // Coordenadas/Localização
    val area = MutableLiveData<String>() // Obrigatório (ex: "C5")
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
        area.value = quadra
        this.sondagem.value = sondagem

        // Define valores padrão se estiver em modo de criação
        if (_artefatoEditavel.value == null) {
            nivel.value = 1
            camada.value = "I"
            // Preencher data atual (ou deixar a função save cuidar disso)
        }
    }

    // 2. Injeta um Artefato existente para edição
    fun setArtifactToEdition(artefato: Artefato?) {
        _artefatoEditavel.value = artefato
        if (artefato == null) {
            // Limpar campos para modo criação (mantém valores de initialQuadra/x/y)
            area.value = _initialQuadra.value ?: ""
            sondagem.value = _initialSondagem.value ?: ""
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
            area.value = artefato.area
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
                // Opcional: Recarrega a lista para atualizar a UI do mapa
                fetchArtifacts()
            } catch (e: Exception) {
                // Tratar erro de exclusão
            }
        }
    }
    fun saveOrUpdateArtifact() {
        // Validação: Verifique os campos essenciais
        if (material.value.isNullOrBlank() || area.value.isNullOrBlank() || nivel.value == null) {
            // Poderia emitir um erro aqui
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            val artefatoFinal: Artefato
            val isUpdating = _artefatoEditavel.value != null

            // 1. Preparação da Data (String Formatada) - Usa a data atual SOMENTE para a CRIAÇÃO
            val dataRegistroFormatada: String
            if (isUpdating) {
                // Mantém a data de registro original ou a data que foi preenchida pelo formulário (se houver um campo de data editável)
                dataRegistroFormatada = _artefatoEditavel.value?.data ?: data.value ?: SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            } else {
                // Novo registro: usa a data e hora atuais
                dataRegistroFormatada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            }

            // 2. Criação/Atualização da Instância
            if (isUpdating) {
                // --- MODO ATUALIZAÇÃO (UPDATE) ---
                val artefatoExistente = _artefatoEditavel.value!!

                // Cria uma nova instância, mantendo IDs e coordenadas originais (Quadra, X, Y)
                artefatoFinal = artefatoExistente.copy(
                    // CAMPOS GERAIS ATUALIZADOS PELO FORMULÁRIO
                    area = area.value!!,
                    sondagem = sondagem.value ?: "N/A", // Permite alteração da sondagem
                    pontoGPS = pontoGPS.value,

                    nivel = nivel.value.toString(),
                    camada = camada.value ?: "I",
                    decapagem = decapagem.value,

                    material = material.value!!,
                    quantidade = quantidade.value ?: 1,

                    pesquisador = pesquisador.value ?: "N/A",
                    obs = obs.value,
                    fotoCaminho = fotoCaminho.value,

                    // Mantém a data de registro original
                    data = dataRegistroFormatada
                )

            } else {
                // --- MODO CRIAÇÃO (CREATE) ---
                artefatoFinal = Artefato(
                    // Geração de IDs e Coordenadas
                    id = UUID.randomUUID().toString(),
                    quadra = _initialQuadra.value!!, // Usa a quadra inicial fixada
                    xRelativo = _initialXRelativo.value!!,
                    yRelativo = _initialYRelativo.value!!,

                    // Dados do Formulário
                    area = area.value!!, // Deve ser igual a quadra no modo de criação, mas é um campo separado
                    sondagem = sondagem.value ?: "N/A",
                    pontoGPS = pontoGPS.value,

                    // Contexto Estratigráfico
                    nivel = nivel.value.toString(),
                    camada = camada.value ?: "I",
                    decapagem = decapagem.value,

                    // Detalhes do Achado
                    material = material.value!!,
                    quantidade = quantidade.value ?: 1,

                    // Logística
                    pesquisador = pesquisador.value ?: "N/A",
                    data = dataRegistroFormatada, // Data formatada
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
                // Logar o erro aqui
            } finally {
                _isLoading.value = false
            }
        }
    }
}