package com.example.projetocoliceu.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.projetocoliceu.data.api.ArtifactApiService
import com.example.projetocoliceu.data.db.ArtefatoDao // Assumindo 'local' para o DAO
import com.example.projetocoliceu.data.db.ArtefatoEntity // Usaremos a Entity no Repositório
import com.example.projetocoliceu.data.model.Artefato
import com.example.projetocoliceu.worker.SyncWorker // Classe que criaremos

import kotlinx.coroutines.flow.Flow

class ArtefatoRepository(
    private val apiService: ArtifactApiService,
    private val dao: ArtefatoDao, // <-- Adicionado o acesso ao DB Local (Room)
    private val context: Context  // <-- Adicionado para o WorkManager
) {

    // -------------------------------------------------------------------------
    // R - READ ALL (Leitura Reativa - SEMPRE LÊ DO ROOM)
    // -------------------------------------------------------------------------

    // Retorna um Flow da lista de Entidades. A UI se inscreve neste Flow.
    fun getAllArtifacts(): Flow<List<ArtefatoEntity>> {
        // Dispara a sincronização em background quando o app pede dados
        startSyncWorker()

        // Retorna a leitura do banco de dados local (Room)
        return dao.getAllArtefatos()
    }

    // -------------------------------------------------------------------------
    // C - CREATE & U - UPDATE (Escrita - SEMPRE ESCREVE PRIMEIRO NO ROOM)
    // -------------------------------------------------------------------------

    // Recebe a Entidade (que já tem o ID local/remoteId)
    // Nota: O Room usa REPLACE, então este método lida com CREATE e UPDATE locais.
    suspend fun saveArtifact(artefato: ArtefatoEntity): ArtefatoEntity {
        // 1. Define o status como PENDENTE DE SINCRONIZAÇÃO (1)
        val pendingArtefato = artefato.copy(syncStatus = 1)

        // 2. Salva a alteração imediatamente no banco de dados local (offline)
        dao.insert(pendingArtefato)

        // 3. Agenda a sincronização para quando houver conexão
        startSyncWorker()

        return pendingArtefato
    }

    // -------------------------------------------------------------------------
    // D - DELETE (Exclusão - MARCA COMO PENDENTE DE EXCLUSÃO)
    // -------------------------------------------------------------------------

    suspend fun deleteArtifact(artefato: ArtefatoEntity) {
        // 1. Marca o item localmente como PENDENTE DE EXCLUSÃO (2)
        // Isso impede que o PULL remoto o re-insira antes que o PUSH o exclua remotamente
        val deletePending = artefato.copy(syncStatus = 2)
        dao.update(deletePending)

        // 2. Agenda a sincronização
        startSyncWorker()
    }


    // -------------------------------------------------------------------------
    // MÉTODOS DE SINCRONIZAÇÃO (Chamados APENAS pelo SyncWorker!)
    // -------------------------------------------------------------------------

    // R - READ ONE (Ainda útil para o Worker buscar um item remoto específico)
    suspend fun fetchArtifactByIdRemote(idFicha: String): Artefato {
        // Usamos o método da API original
        return apiService.fetchArtifactById(idFicha)
    }

    // O WorkManager usará estes métodos para o PUSH/PULL
    suspend fun createArtifactRemote(artefato: Artefato): Artefato = apiService.createArtifact(artefato)
    suspend fun updateArtifactRemote(artefato: Artefato): Artefato = apiService.updateArtifact(artefato.idCartao, artefato)
    suspend fun deleteArtifactRemote(idFicha: String) = apiService.deleteArtifact(idFicha)
    suspend fun fetchAllArtifactsRemote(): List<Artefato> = apiService.fetchAllArtifacts()


    // -------------------------------------------------------------------------
    // WORKER AGENDAMENTO (Função utilitária)
    // -------------------------------------------------------------------------

    private fun startSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Só executa com internet
            .build()

        // Cria uma requisição de trabalho que será executada uma única vez
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        // Coloca a requisição na fila do WorkManager
        WorkManager.getInstance(context).enqueue(syncRequest)
    }
}