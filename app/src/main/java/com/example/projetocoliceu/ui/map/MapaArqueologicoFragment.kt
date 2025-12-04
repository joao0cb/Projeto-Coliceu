package com.example.projetocoliceu.ui.map

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs // <-- IMPORTANTE
import com.example.projetocoliceu.R
import com.example.projetocoliceu.data.repository.ArtefatoRepository
import com.example.projetocoliceu.databinding.FragmentMapBinding
import com.example.projetocoliceu.viewmodel.ArtifactViewModel
import com.example.projetocoliceu.viewmodel.MapViewModel
import com.example.projetocoliceu.viewmodel.MapViewModelFactory
import com.example.projetocoliceu.data.api.RetrofitClient
import com.example.projetocoliceu.data.db.AppDatabase
import androidx.fragment.app.viewModels
import com.example.projetocoliceu.data.repository.MapRepository
import com.example.projetocoliceu.viewmodel.ArtifactViewModelFactory

class MapaArqueologicoFragment : Fragment(R.layout.fragment_map) {

    private var hasSaved = false
    private val args by navArgs<MapaArqueologicoFragmentArgs>() // <-- AQUI

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val inputStream = requireContext().contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                binding.map.setBackgroundImage(bitmap)
            }
        }
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
                RetrofitClient.apiService,
                AppDatabase.getDatabase(requireContext()).artefatoDao(),
                requireContext()
            ),
            MapRepository(
                AppDatabase.getDatabase(requireContext()).mapDao()
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
        super.onViewCreated(view, savedInstanceState)

        val mapId = args.mapId  // <-- 🎯 Pegando o argumento enviado
        mapViewModel.selectMap(mapId)
        mapViewModel.getArtifactsByMap(mapId)


        val customMap = binding.map
        customMap.setViewModel(mapViewModel)

        mapViewModel.artefatos.observe(viewLifecycleOwner) { lista ->
            customMap.setArtefatos(lista)
        }

        customMap.onArtefatoClick = { artefato ->
            artifactViewModel.setArtifactToEdition(artefato)
            artifactViewModel.setInitialCoordinates(
                artefato.quadra,
                artefato.xRelativo,
                artefato.yRelativo,
                artefato.sondagem
            )
            findNavController().navigate(R.id.action_mapaArqueologico_to_artifactDetail)
        }

        customMap.onNovoArtefatoClick = { quadra, xRel, yRel ->
            artifactViewModel.clearEditionIfNeeded()
            artifactViewModel.setInitialCoordinates(quadra, xRel, yRel)
            artifactViewModel.resetSaveSuccess()

            hasSaved = false

            val mapId = mapViewModel.currentMapId.value ?: return@onNovoArtefatoClick

            val action = MapaArqueologicoFragmentDirections
                .actionMapaArqueologicoToArtifactDetail(mapId = mapId)

            findNavController().navigate(action)
        }

        binding.btnVoltar.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.btnListarArtefatos.setOnClickListener {
            val action = MapaArqueologicoFragmentDirections
                .actionMapaArqueologicoToArtifactList()
            findNavController().navigate(action)
        }

        binding.btnAdicionarBackground.setOnClickListener {
            // Aqui você pode pegar a imagem de recursos ou abrir um seletor de arquivos
            selectImageLauncher.launch("image/*")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
