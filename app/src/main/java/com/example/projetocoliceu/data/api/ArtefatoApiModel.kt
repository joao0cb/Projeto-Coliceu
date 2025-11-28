package com.example.projetocoliceu.data.api

import com.example.projetocoliceu.data.model.Artefato

data class ArtefatoApiModel(
    val id: String,
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

fun ArtefatoApiModel.toDomain(): Artefato {
    return Artefato(
        id = id,
        quadra = quadra,
        area = area,
        sondagem = sondagem,
        pontoGPS = pontoGPS,
        nivel = nivel,
        camada = camada,
        decapagem = decapagem,
        material = material,
        quantidade = quantidade,
        data = data,
        pesquisador = pesquisador,
        obs = obs,
        xRelativo = xRelativo,
        yRelativo = yRelativo,
        fotoCaminho = fotoCaminho
    )
}

fun Artefato.toApiModel(): ArtefatoApiModel {
    return ArtefatoApiModel(
        id = id,
        quadra = quadra,
        area = area,
        sondagem = sondagem,
        pontoGPS = pontoGPS,
        nivel = nivel,
        camada = camada,
        decapagem = decapagem,
        material = material,
        quantidade = quantidade,
        data = data,
        pesquisador = pesquisador,
        obs = obs,
        xRelativo = xRelativo,
        yRelativo = yRelativo,
        fotoCaminho = fotoCaminho
    )
}
