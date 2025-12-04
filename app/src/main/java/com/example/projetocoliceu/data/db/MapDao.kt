package com.example.projetocoliceu.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MapDao {

    @Query("SELECT * FROM maps")
    fun getAllMaps(): Flow<List<MapEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMap(map: MapEntity)

    @Delete
    suspend fun deleteMap(map: MapEntity)

    @Query("SELECT * FROM artefatos WHERE mapId = :mapId")
    fun getArtifactsByMap(mapId: String): Flow<List<ArtefatoEntity>>
}
