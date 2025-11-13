package com.example.projetocoliceu

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.viewModels
// Necessário para encontrar IDs de recursos (R.id.mapaArqueologicoView)
import com.example.projetocoliceu.R
import com.example.projetocoliceu.data.api.RetrofitClient
import com.example.projetocoliceu.data.repository.ArtefatoRepository
import com.example.projetocoliceu.ui.map.MapaArqueologico
import com.example.projetocoliceu.viewmodel.MapViewModel
import com.example.projetocoliceu.viewmodel.MapViewModelFactory // IMPORT NECESSÁRIO!
import com.example.projetocoliceu.viewmodel.ArtifactViewModel // Opcional por enquanto, mas mantido

class MainActivity : AppCompatActivity() {

    // 1. Inicializa o Repositório (ArtefatoRepository)
    // O erro ArtefatoRepository(RetrofitClient.apiService) foi resolvido com os imports acima.
    private val repository by lazy {
        ArtefatoRepository(RetrofitClient.apiService)
    }

    // 2. Inicializa o MapViewModel usando a Factory
    // O erro MapViewModelFactory(repository) foi resolvido com o import acima.
    private val mapViewModel: MapViewModel by viewModels {
        MapViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // MANTIDO: Configuração de borda e set do layout
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Conexão do Mapa com a Activity
        // O ID 'mapaArqueologicoView' é o que está no seu activity_main.xml
        val mapaView: MapaArqueologico = findViewById(R.id.mapaArqueologicoView)

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