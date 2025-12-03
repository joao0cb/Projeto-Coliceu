package com.example.projetocoliceu.data.api

import retrofit2.http.*

interface ArtifactApiService {

    @GET("artefatos")
    suspend fun fetchAllArtifacts(): List<ArtefatoApiModel>

    @POST("artefatos")
    suspend fun createArtifact(
        @Body artefato: ArtefatoApiModel
    ): ArtefatoApiModel

    @PUT("artefatos/{id}")
    suspend fun updateArtifact(
        @Path("id") id: String,
        @Body artefato: ArtefatoApiModel
    ): ArtefatoApiModel

    @DELETE("artefatos/{id}")
    suspend fun deleteArtifact(
        @Path("id") id: String
    )
}
