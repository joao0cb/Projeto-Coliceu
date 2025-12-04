package com.example.projetocoliceu.ui.artifact

import com.example.projetocoliceu.data.api.RetrofitClient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetocoliceu.databinding.FragmentArtifactListBinding
import com.example.projetocoliceu.data.repository.ArtefatoRepository
import com.example.projetocoliceu.data.repository.MapRepository
import com.example.projetocoliceu.data.db.AppDatabase
import com.example.projetocoliceu.viewmodel.ArtifactViewModel
import com.example.projetocoliceu.viewmodel.ArtifactViewModelFactory
import com.example.projetocoliceu.viewmodel.MapViewModel
import com.example.projetocoliceu.viewmodel.MapViewModelFactory
import com.google.android.material.snackbar.Snackbar

class ArtifactListFragment : Fragment() {

    private var _binding: FragmentArtifactListBinding? = null
    private val binding get() = _binding!!

    private val artifactViewModel: ArtifactViewModel by activityViewModels {
        ArtifactViewModelFactory(
            ArtefatoRepository(
                apiService = RetrofitClient.apiService,
                dao = AppDatabase.getDatabase(requireContext()).artefatoDao(),
                context = requireContext()
            )
        )
    }
    // Shared ViewModel entre telas
    private val viewModel: MapViewModel by activityViewModels {
        val database = AppDatabase.getDatabase(requireContext())
        MapViewModelFactory(
            ArtefatoRepository(
                apiService = RetrofitClient.apiService,                       // sua API
                dao = AppDatabase.getDatabase(requireContext()).artefatoDao(),
                context = requireContext()
            ),
            MapRepository(database.mapDao())
        )
    }

    private lateinit var adapter: ArtifactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtifactListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ArtifactAdapter { artefato ->
            // Abrir detalhes do artefato
            val action =
                ArtifactListFragmentDirections
                    .actionArtifactListToArtifactDetail()
            findNavController().navigate(action)
        }

        binding.recyclerViewArtifacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewArtifacts.adapter = adapter

        // Observa mudanças nos artefatos do mapa
        viewModel.artefatos.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        // Força recarregar ao entrar
        viewModel.currentMapId.value?.let { mapId ->
            viewModel.getArtifactsByMap(mapId)
        } ?: Snackbar.make(view, "Erro: Nenhum mapa selecionado!", Snackbar.LENGTH_LONG).show()

        binding.btnVoltar.setOnClickListener{
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
