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

    // --- MÉTODOS DE LEITURA REATIVA (Para o ViewModel) ---

    // Busca todos os artefatos. Retorna Flow para que a UI seja atualizada automaticamente.
    @Query("SELECT * FROM artefatos")
    fun getAllArtefatos(): Flow<List<ArtefatoEntity>>

    // Busca um artefato pelo ID local (se necessário)
    @Query("SELECT * FROM artefatos WHERE idCartao = :id")
    suspend fun getArtefatoById(id: String): ArtefatoEntity?


    // --- MÉTODOS DE ESCRITA/BUSCA PONTUAL (Para o Repository e Worker) ---

    // Insere um novo artefato. Usa REPLACE para atualizar se o ID (UUID) for o mesmo.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artefato: ArtefatoEntity)

    // Atualiza um artefato existente.
    @Update
    suspend fun update(artefato: ArtefatoEntity)

    // Deleta um artefato.
    @Delete
    suspend fun delete(artefato: ArtefatoEntity)

    // Deleta um artefato pelo ID. Útil para o SyncWorker após exclusão remota.
    @Query("DELETE FROM artefatos WHERE idCartao = :id")
    suspend fun deleteById(id: String)


    // --- MÉTODOS DE SINCRONIZAÇÃO (Para o SyncWorker) ---

    /**
     * Busca itens com syncStatus diferente de 0 (Sincronizado).
     * syncStatus = 1 (Pendente de Criação/Atualização)
     * syncStatus = 2 (Pendente de Exclusão)
     */
    @Query("SELECT * FROM artefatos WHERE syncStatus != 0")
    suspend fun getPendingSyncItems(): List<ArtefatoEntity>
}