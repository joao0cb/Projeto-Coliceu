package com.example.projetocoliceu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData // Essencial para converter Flow -> LiveData
import androidx.lifecycle.viewModelScope
import com.example.projetocoliceu.data.db.MapEntity
import com.example.projetocoliceu.data.model.Map
import com.example.projetocoliceu.data.model.Artefato
import com.example.projetocoliceu.data.model.toArtefatoModel
import com.example.projetocoliceu.data.model.toEntityModel
import com.example.projetocoliceu.data.repository.ArtefatoRepository
import com.example.projetocoliceu.data.repository.MapRepository
import kotlinx.coroutines.launch
import java.util.UUID

class MapViewModel(
    private val repository: ArtefatoRepository,
    private val repositoryMap: MapRepository
) : ViewModel() {

    private val _artefatos = MutableLiveData<List<Artefato>>()
    val artefatos: LiveData<List<Artefato>> = _artefatos

    val maps = repositoryMap.getAllMaps().asLiveData()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _currentMapId = MutableLiveData<String>()
    val currentMapId: LiveData<String> = _currentMapId

    fun selectMap(mapId: String) {
        _currentMapId.value = mapId
    }

    fun getArtifactsByMap(mapId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getArtifactsByMap(mapId)  // ✔ agora acessa o repositório certo
                .collect { entities ->
                    _artefatos.postValue(entities.map { it.toArtefatoModel() })
                    _isLoading.postValue(false)
                }
        }
    }

    fun createMap(name: String) {
        val newMap = Map(name = name).toEntityModel()
        viewModelScope.launch {
            repositoryMap.insertMap(newMap)
        }
    }

    fun deleteMap(map: MapEntity) {
        viewModelScope.launch {
            repositoryMap.deleteMap(map)
        }
    }
}


