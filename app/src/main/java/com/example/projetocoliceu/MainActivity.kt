package com.example.projetocoliceu

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.viewModels
// Necessário para encontrar IDs de recursos (R.id.mapaArqueologicoView)
import com.example.projetocoliceu.data.db.AppDatabase
import com.example.projetocoliceu.data.api.RetrofitClient
import com.example.projetocoliceu.data.repository.ArtefatoRepository
import com.example.projetocoliceu.ui.map.MapaArqueologico
import com.example.projetocoliceu.viewmodel.MapViewModel
import com.example.projetocoliceu.viewmodel.MapViewModelFactory // IMPORT NECESSÁRIO!

class MainActivity : AppCompatActivity() {

    // 1. Inicializa o Database (Room)
    private val database by lazy {
        AppDatabase.getDatabase(applicationContext)
    }

    // 2. Inicializa o Repositório, passando o DAO e o Context
    private val repository by lazy {
        ArtefatoRepository(
            RetrofitClient.apiService,
            database.artefatoDao(), // Agora o 'dao' está resolvido
            applicationContext      // Agora o 'context' está resolvido
        )
    }

    // 3. Inicializa o MapViewModel usando a Factory
    private val mapViewModel: MapViewModel by viewModels {
        MapViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ... (código onCreate omitido por brevidade)

        enableEdgeToEdge()
        setContentView(R.layout.activity_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mapaArqueologico)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Conexão do Mapa com a Activity
        // O ID 'mapaArqueologicoView' é o que está no seu activity_main.xml
        val mapaView: MapaArqueologico = findViewById(R.id.mapa)

        // 4. Injetar o ViewModel no Mapa
        mapaView.setViewModel(mapViewModel)

        // 5. Observar os dados do ViewModel
        mapViewModel.artefatos.observe(this) { artefatos ->
            mapaView.setArtefatos(artefatos)
        }

        // 6. Observar o carregamento
        mapViewModel.isLoading.observe(this) { isLoading ->
            // Lógica para mostrar/esconder um ProgressBar.
        }
    }
}