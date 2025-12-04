package com.example.projetocoliceu.data.repository

import com.example.projetocoliceu.data.db.ArtefatoEntity
import com.example.projetocoliceu.data.db.MapEntity
import com.example.projetocoliceu.data.db.MapDao
import com.example.projetocoliceu.data.model.Artefato
import com.example.projetocoliceu.data.model.toArtefatoEntity
import com.example.projetocoliceu.data.model.toArtefatoModel
import com.example.projetocoliceu.data.repository.ArtefatoRepository.Companion.PENDING_SYNC
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MapRepository(private val dao: MapDao) {

    fun getAllMaps(): Flow<List<MapEntity>> =
        dao.getAllMaps()

    suspend fun insertMap(map: MapEntity) {
        dao.insertMap(map)
    }

    suspend fun deleteMap(map: MapEntity) {
        dao.deleteMap(map)
    }
}
