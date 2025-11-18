// ArtifactApiService.kt (Corrigido)
package com.example.projetocoliceu.data.api

import com.example.projetocoliceu.data.db.ArtefatoEntity
import com.example.projetocoliceu.data.model.Artefato
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ArtifactApiService {

    // Buscar todos
    @GET("api/artifacts")
    suspend fun fetchAllArtifacts(): List<ArtefatoEntity>

    // Criar (POST)
    @POST("api/artifacts")
    suspend fun createArtifact(@Body ficha: Artefato): Artefato

    // Atualizar (PUT)
    @PUT("api/artifacts/{id}")
    suspend fun updateArtifact(@Path("id") id: String, @Body ficha: Artefato): Artefato

    // Deletar (DELETE)
    @DELETE("api/artifacts/{id}")
    suspend fun deleteArtifact(@Path("id") id: String)

    // Buscar por ID (GET) - Função corrigida para ser suspend e usar Path
    @GET("api/artifacts/{id}")
    suspend fun fetchArtifactById(@Path("id") id: String): Artefato
}