package com.example.projetocoliceu.data.model

data class Artefato(
    // 1. Identificação Espacial (Para o mapa e localização)
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
    val quantidade: Int = 1,

    // 4. Logística e Registro
    val data: String,
    val pesquisador: String,
    val obs: String?,

    // Campos Internos
    val idCartao: String,

    // Coordenadas relativas para posicionar no CustomView (mapa)
    val xRelativo: Float, // Posição X dentro da quadra (0.0 a 1.0)
    val yRelativo: Float
 )