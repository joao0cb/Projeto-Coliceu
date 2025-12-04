package com.example.projetocoliceu.data.model

import com.example.projetocoliceu.data.db.MapEntity
import java.util.UUID

data class Map(
    val id: String = UUID.randomUUID().toString(),
    val name: String
)

// Mapeamento Domain -> DB
fun Map.toEntityModel(): MapEntity {
    return MapEntity(
        id = this.id,
        name = this.name
    )
}