package com.example.projetocoliceu.data.db

import com.example.projetocoliceu.data.model.Artefato
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// MUDE PARA data class
@Entity(tableName = "artefatos")
data class ArtefatoEntity(
    // 1. Identificação
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),


    val quadra: String,
    val area: String,
    val sondagem: String,
    val pontoGPS: String?,

    // 2. Contexto Estratigráfico (Onde o achado estava)
    val nivel: String,
    val camada: String,
    val decapagem: String?,

    // 3. Informações do achado
    val material: String,
    val quantidade: Int = 1, // Pode ter um valor padrão

    // 4. Logística e Registro
    val data: String,
    val pesquisador: String,
    val obs: String?,

    // Coordenadas relativas para posicionar no CustomView (mapa)
    val xRelativo: Float,
    val yRelativo: Float,
    val fotoCaminho: String?,

    val syncStatus: Int

)

