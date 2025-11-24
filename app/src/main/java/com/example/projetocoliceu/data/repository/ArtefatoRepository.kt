package com.example.projetocoliceu.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.projetocoliceu.data.model.toArtefatoModel
import com.example.projetocoliceu.data.model.toArtefatoEntity
import com.example.projetocoliceu.data.model.Artefato
import com.example.projetocoliceu.data.db.ArtefatoDao
import com.example.projetocoliceu.data.api.ArtifactApiService
import com.example.projetocoliceu.data.db.ArtefatoEntity
import com.example.projetocoliceu.worker.SyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class ArtefatoRepository(
    private val apiService: ArtifactApiService,
    private val dao: ArtefatoDao,
    private val context: Context
) {

    // Constantes para o WorkManager e Status de Sincronização
    companion object {
        const val PENDING_SYNC = 1
        const val PENDING_DELETE = 2
        const val SYNCED = 0 // Adicionado para clareza
    }

    // R - READ ALL
    fun getAllArtifacts(): Flow<List<Artefato>> {
        startSyncWorker()
        return dao.getAllArtefatos()
            .map { entities ->
                entities.map { it.toArtefatoModel() } // Corrigido o tipo de retorno
            }
    }

    // C - CREATE & U - UPDATE (Local)
    suspend fun saveArtifact(artefato: Artefato): Artefato {
        // CORREÇÃO: Passando explicitamente o syncStatus (Corrigindo "No value passed for parameter 'syncStatus'")
        val entityToSave = artefato.toArtefatoEntity(PENDING_SYNC)
        dao.insert(entityToSave)
        startSyncWorker()
        return entityToSave.toArtefatoModel()
    }

    // D - DELETE (Marca Local)
    suspend fun deleteArtifact(artefato: Artefato) {
        dao.deleteById(artefato.id) // remove imediatamente
        startSyncWorker()
    }

    // MÉTODOS AUXILIARES (Para o Worker)
    suspend fun getPendingSyncArtifacts(): List<Artefato> {
        val pendingEntities = dao.getPendingSyncArtefatos()
        return pendingEntities.map { entity ->
            entity.toArtefatoModel()
        }
    }


    suspend fun fetchAllArtifactsRemote(): List<Artefato> {
        // CORREÇÃO: Mapeando ArtefatoEntity para Artefato após a chamada da API
        return apiService.fetchAllArtifacts().map { it.toArtefatoModel() }
    }

    /**
     * PUSH: Envia um novo artefato para o servidor.
     */
    suspend fun createArtifactRemote(artefatoEntity: ArtefatoEntity): Artefato {
        val artefatoModel = artefatoEntity.toArtefatoModel()
        return apiService.createArtifact(artefatoModel)
    }

    /**
     * PUSH: Atualiza um artefato existente no servidor.
     */
    suspend fun updateArtifactRemote(artefatoEntity: ArtefatoEntity): Artefato {
        val artefatoModel = artefatoEntity.toArtefatoModel()
        return apiService.updateArtifact(artefatoEntity.id, artefatoModel)
    }

    suspend fun deleteArtifactRemote(idCartao: String) = apiService.deleteArtifact(idCartao)

    /**
     * Atualiza um artefato localmente e marca para sincronização.
     * Lembre-se: este método é 'suspend' — chame dentro de coroutine (viewModelScope.launch {...}).
     */
    suspend fun updateArtifact(artefato: Artefato): Artefato {
        // Converte o modelo para entidade (passando o status de sync pendente)
        val entityToUpdate = artefato.toArtefatoEntity(PENDING_SYNC)

        // Verifica se o registro existe localmente (opcional, mas útil para evitar surpresas)
        val existing = dao.getArtefatoById(entityToUpdate.id)
        if (existing == null) {
            // Se você preferir, pode inserir em vez de lançar
            // dao.insert(entityToUpdate)
            // ou lançar para indicar erro:
            throw IllegalStateException("Artefato com id ${entityToUpdate.id} não encontrado para atualização.")
        }

        // Atualiza no banco local (Room)
        dao.update(entityToUpdate)

        // Agenda sincronização (WorkManager)
        startSyncWorker()

        // Retorna o modelo atualizado
        return entityToUpdate.toArtefatoModel()
    }


    // WORKER AGENDAMENTO
    private fun startSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .addTag("sync_artefato_data") // Boa prática: adicione uma tag
            .build()

        // Usa o ENQUEUE_UNIQUE_WORK para evitar agendar múltiplos workers
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "sync_artefato_data",
                ExistingWorkPolicy.KEEP,
                syncRequest
            )

    }
}