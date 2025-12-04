package com.example.projetocoliceu.data.api

import com.example.projetocoliceu.data.model.Artefato

data class ArtefatoApiModel(
    val id: String,
    val nome: String,
    val mapId: String,
    val quadra: String,
    val area: String,
    val sondagem: String,
    val pontoGPS: String?,

    val nivel: String,
    val camada: String,
    val decapagem: String?,

    val material: String,
    val quantidade: Int,

    val data: String,
    val pesquisador: String,
    val obs: String?,

    val xRelativo: Float,
    val yRelativo: Float,
    val fotoCaminho: String?
)