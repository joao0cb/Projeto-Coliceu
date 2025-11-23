package com.example.projetocoliceu.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projetocoliceu.R
import com.example.projetocoliceu.data.repository.ArtefatoRepository
import com.example.projetocoliceu.databinding.FragmentMapBinding
import com.example.projetocoliceu.viewmodel.ArtifactViewModel
import com.example.projetocoliceu.viewmodel.MapViewModel
import com.example.projetocoliceu.viewmodel.MapViewModelFactory
import com.example.projetocoliceu.data.api.RetrofitClient
import com.example.projetocoliceu.data.db.AppDatabase
import androidx.fragment.app.viewModels
import com.example.projetocoliceu.viewmodel.ArtifactViewModelFactory

class MapaArqueologicoFragment : Fragment(R.layout.fragment_map) {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    // Se usa um MapViewModel separado (opcional). Mas necessário: ArtifactViewModel para compartilhar dados de edição.
    private val artifactViewModel: ArtifactViewModel by activityViewModels{
        ArtifactViewModelFactory(
            ArtefatoRepository(
                apiService = RetrofitClient.apiService,
                dao = AppDatabase.getDatabase(requireContext()).artefatoDao(),
                context = requireContext()
            )
        )
    }
    private val mapViewModel: MapViewModel by viewModels {
        MapViewModelFactory(
            ArtefatoRepository(
                apiService = RetrofitClient.apiService,
                dao = AppDatabase.getDatabase(requireContext()).artefatoDao(),
                context = requireContext()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val customMap = binding.map

        customMap.setViewModel(mapViewModel)

        // Atualiza o mapa quando os artefatos mudarem
        mapViewModel.artefatos.observe(viewLifecycleOwner) { lista ->
            customMap.setArtefatos(lista)
        }

        // Clique em artefato existente → editar
        customMap.onArtefatoClick = { artefato ->
            artifactViewModel.setArtifactToEdition(artefato)
            artifactViewModel.setInitialCoordinates(
                artefato.quadra,
                artefato.xRelativo,
                artefato.yRelativo,
                artefato.sondagem
            )

            findNavController().popBackStack(R.id.MapaArqueologicoFragment, false)
            findNavController().navigate(
                R.id.action_MapaArqueologicoFragment_to_ArtifactDetailFragment
            )
        }

        // Clique em área vazia → criar novo
        customMap.onNovoArtefatoClick = { quadra, xRel, yRel ->
            artifactViewModel.clearEditionIfNeeded()      // limpa artefato atual
            artifactViewModel.setInitialCoordinates(quadra, xRel, yRel)
            artifactViewModel.resetSaveSuccess()          // reseta o LiveData de sucesso

            findNavController().popBackStack(R.id.MapaArqueologicoFragment, false)
            findNavController().navigate(
                R.id.action_MapaArqueologicoFragment_to_ArtifactDetailFragment
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
