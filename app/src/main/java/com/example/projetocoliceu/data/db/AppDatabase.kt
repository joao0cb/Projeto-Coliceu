// AppDatabase.kt
package com.example.projetocoliceu.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Certifique-se de listar todas as suas entidades aqui
@Database(entities = [ArtefatoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Método que o Repositório irá chamar para obter o DAO
    abstract fun artefatoDao(): ArtefatoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Se a instância já existe, retorna ela
            return INSTANCE ?: synchronized(this) {
                // Se a instância é nula, cria o banco de dados
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "artefato_database"
                )
                    // Se você não tiver migrações, pode usar .fallbackToDestructiveMigration()
                    // para fins de desenvolvimento
                    // .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}