package com.example.projetocoliceu.data.repository

import com.example.projetocoliceu.data.api.ArtifactApiService
import com.example.projetocoliceu.data.model.Artefato

// com.example.projetocoliceu.data.repository/ArtefatoRepository.kt

class ArtefatoRepository(private val apiService: ArtifactApiService) {

    // R - READ ALL (Leitura de todos)
    suspend fun getAllArtifacts(): List<Artefato> {
        return apiService.fetchAllArtifacts()
    }

    // C - CREATE (Criação)
    suspend fun saveArtifact(artefato: Artefato): Artefato {
        // Você já está usando isso no ArtifactViewModel
        return apiService.createArtifact(artefato)
    }

    // R - READ ONE (Leitura de um único artefato pelo ID)
    suspend fun getArtifactById(idFicha: String): Artefato {
        return apiService.fetchArtifactById(idFicha)
    }

    // U - UPDATE (Atualização)
    suspend fun updateArtifact(artefato: Artefato): Artefato {
        // Geralmente usa PUT ou PATCH no endpoint do Spring Boot
        return apiService.updateArtifact(artefato.idCartao, artefato)
    }

    // D - DELETE (Exclusão)
    suspend fun deleteArtifact(idFicha: String) {
        apiService.deleteArtifact(idFicha)
    }
}