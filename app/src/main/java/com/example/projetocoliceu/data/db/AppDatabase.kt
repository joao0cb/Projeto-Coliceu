// AppDatabase.kt
package com.example.projetocoliceu.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.projetocoliceu.data.db.UserEntity

// ATUALIZE A LISTA DE ENTIDADES e INCREMENTE A VERSÃO!
@Database(entities = [ArtefatoEntity::class, UserEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun artefatoDao(): ArtefatoDao
    // ADICIONE O NOVO DAO
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // Renomeei para ser mais genérico que "artefato_database"
                )
                    // Importante: Se você alterar a versão do banco (de 1 para 2),
                    // e não tiver migrações, use fallbackToDestructiveMigration()
                    // durante o desenvolvimento para evitar crashs.
                    .fallbackToDestructiveMigration() // <--- ADICIONADO/ALTERADO
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}