package com.example.projetocoliceu.data.api

import com.example.projetocoliceu.data.db.ArtefatoEntity
import com.example.projetocoliceu.data.model.Artefato
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


// Interface que define os endpoints (URLs) que o app vai chamar no Spring Boot
interface ArtifactApiService {
    // CHAMA O ENDPOINT GET: /api/artifacts
    // Usado para buscar todos os artefatos do servidor.
    @GET("api/artifacts")
    // 'suspend' indica que esta é uma função assíncrona (não trava a interface)
    suspend fun fetchAllArtifacts(): List<ArtefatoEntity>

    // CHAMA O ENDPOINT POST: /api/artifacts
    // Usado para enviar uma nova ficha (Artefato) para o servidor salvar.
    @POST("api/artifacts")
    // @Body indica que o objeto 'ficha' deve ser enviado no corpo da requisição em JSON
    suspend fun createArtifact(@Body ficha: Artefato): Artefato

    // Sugestão: Você também precisará de funções para atualizar (PUT) e deletar (DELETE)
    @PUT("api/artifacts/{id}")
    suspend fun updateArtifact(@Path("id") id: String, @Body ficha: Artefato): Artefato

    @DELETE("api/v1/artefatos/{id}") // Assumindo que seu Spring Boot usa /api/v1/artefatos/{id}
    suspend fun deleteArtifact(@Path("id") id: String)
    fun fetchArtifactById(idCartao: String): Artefato
}