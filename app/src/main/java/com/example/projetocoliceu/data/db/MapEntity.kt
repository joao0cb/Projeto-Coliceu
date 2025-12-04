package com.example.projetocoliceu.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "maps")
data class MapEntity (
    @PrimaryKey
    val id: String,
    val name: String
)