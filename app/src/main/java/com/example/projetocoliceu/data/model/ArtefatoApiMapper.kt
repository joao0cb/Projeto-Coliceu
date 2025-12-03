package com.example.projetocoliceu.data.model

import com.example.projetocoliceu.data.api.ArtefatoApiModel

/**
 * API → Domain
 */
fun ArtefatoApiModel.toDomain(): Artefato {
    return Artefato(
        id = this.id,
        quadra = this.quadra,
        area = this.area,
        sondagem = this.sondagem,
        pontoGPS = this.pontoGPS,

        nivel = this.nivel,
        camada = this.camada,
        decapagem = this.decapagem,

        material = this.material,
        quantidade = this.quantidade,

        data = this.data,
        pesquisador = this.pesquisador,
        obs = this.obs,

        xRelativo = this.xRelativo,
        yRelativo = this.yRelativo,
        fotoCaminho = this.fotoCaminho
    )
}

/**
 * Domain → API
 */
fun Artefato.toApiModel(): ArtefatoApiModel {
    return ArtefatoApiModel(
        id = this.id,
        quadra = this.quadra,
        area = this.area,
        sondagem = this.sondagem,
        pontoGPS = this.pontoGPS,

        nivel = this.nivel,
        camada = this.camada,
        decapagem = this.decapagem,

        material = this.material,
        quantidade = this.quantidade,

        data = this.data,
        pesquisador = this.pesquisador,
        obs = this.obs,

        xRelativo = this.xRelativo,
        yRelativo = this.yRelativo,
        fotoCaminho = this.fotoCaminho
    )
}
