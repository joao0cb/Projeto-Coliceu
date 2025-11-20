package com.example.projetocoliceu.data.model

import jakarta.persistence.*
import com.example.projetocoliceu.data.db.ArtefatoEntity
import java.util.UUID


@Entity
@Table(name = "artefatos")
data class Artefato(
    // 1. Identificação Espacial (Para o mapa e localização)
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
    val quantidade: Int = 1,

    // 4. Logística e Registro
    val data: String,
    val pesquisador: String,
    val obs: String?,

    // Coordenadas relativas para posicionar no CustomView (mapa)
    val xRelativo: Float, // Posição X dentro da quadra (0.0 a 1.0)
    val yRelativo: Float,
    val fotoCaminho: String?
)

fun Artefato.toArtefatoEntity(syncStatus: Int): ArtefatoEntity {
    return ArtefatoEntity(
        id = this.id, // AGORA O NOME ESTÁ PADRONIZADO
        quadra = this.quadra,
        area = this.area,
        // ** CAMPOS FALTANTES ADICIONADOS **
        sondagem = this.sondagem,
        pontoGPS = this.pontoGPS,
        camada = this.camada,
        decapagem = this.decapagem,
        quantidade = this.quantidade,
        fotoCaminho = this.fotoCaminho,
        // ---------------------------------
        nivel = this.nivel,
        material = this.material,
        xRelativo = this.xRelativo,
        yRelativo = this.yRelativo,
        data = this.data,
        pesquisador = this.pesquisador,
        obs = this.obs,
        syncStatus = syncStatus
    )
}

// --- Mapeamento DB -> UI (Usado em getAllArtifacts) ---
fun ArtefatoEntity.toArtefatoModel(): Artefato {
    return Artefato(
        id = this.id,
        quadra = this.quadra,
        area = this.area,
        // ** CAMPOS FALTANTES ADICIONADOS **
        sondagem = this.sondagem,
        pontoGPS = this.pontoGPS,
        camada = this.camada,
        decapagem = this.decapagem,
        quantidade = this.quantidade,
        fotoCaminho = this.fotoCaminho,
        // ---------------------------------
        nivel = this.nivel,
        material = this.material,
        xRelativo = this.xRelativo,
        yRelativo = this.yRelativo,
        data = this.data,
        pesquisador = this.pesquisador,
        obs = this.obs
    )
}
