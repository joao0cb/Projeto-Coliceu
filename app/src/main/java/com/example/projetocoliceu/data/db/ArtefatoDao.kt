package com.example.projetocoliceu.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.projetocoliceu.data.db.ArtefatoEntity // Use a Entidade corrigida
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtefatoDao {

    @Query("SELECT * FROM artefatos")
    fun getAllArtefatos(): Flow<List<ArtefatoEntity>>

    @Query("SELECT * FROM artefatos WHERE id = :id")
    suspend fun getArtefatoById(id: String): ArtefatoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artefato: ArtefatoEntity)

    @Update
    suspend fun update(artefato: ArtefatoEntity)

    @Delete
    suspend fun delete(artefato: ArtefatoEntity)

    @Query("DELETE FROM artefatos WHERE id = :id")
    suspend fun deleteById(id: String)


    // --- CORRETO: lista de pendências ---
    @Query("SELECT * FROM artefatos WHERE syncStatus != 0")
    suspend fun getPendingSyncArtefatos(): List<ArtefatoEntity>
}
