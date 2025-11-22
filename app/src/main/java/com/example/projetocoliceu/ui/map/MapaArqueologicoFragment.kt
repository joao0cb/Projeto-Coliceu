package com.example.projetocoliceu.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projetocoliceu.R
import com.example.projetocoliceu.data.model.Artefato
import com.example.projetocoliceu.databinding.FragmentMapBinding
import com.example.projetocoliceu.viewmodel.ArtifactViewModel
import com.example.projetocoliceu.viewmodel.MapViewModel

class MapaArqueologicoFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    // Se usa um MapViewModel separado (opcional). Mas necessário: ArtifactViewModel para compartilhar dados de edição.
    private val artifactViewModel: ArtifactViewModel by activityViewModels()
    private val mapViewModel: MapViewModel by activityViewModels() // ou viewModels() conforme injeção

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Configura o custom view e observa dados
        val customMap = binding.map

        // Injeta o viewmodel no custom view (opcional, seu custom view tem setViewModel)
        customMap.setViewModel(mapViewModel)

        // Observa a lista de artefatos do MapViewModel e passa para o custom view
        mapViewModel.artefatos.observe(viewLifecycleOwner) { lista ->
            customMap.setArtefatos(lista)
        }

        // Callback: quando o custom view detecta clique em artefato
        customMap.onArtefatoClick = { artefato ->
            // Injeta o artefato no ArtifactViewModel para edição
            artifactViewModel.setArtifactToEdition(artefato)

            // Opcional: também setar coordenadas iniciais para o formulário (se desejar)
            artifactViewModel.setInitialCoordinates(artefato.quadra, artefato.xRelativo, artefato.yRelativo, artefato.sondagem)

            // Navega para o fragmento de detalhe (usando nav component)
            findNavController().navigate(R.id.action_MapArqueologicoFragment_to_ArtifactDetailFragment)
        }

        // Botão fab para criar novo artefato via MapViewModel (se desejar)
        binding.btnAdicionar.setOnClickListener {
            // Aqui você pode abrir modo criação - por exemplo posicionamento manual ou padrão
            // Exemplo simples: cria artefato vazio com quadra A1 e cai no formulário
            mapViewModel.startNewArtefato("A1", 0.5f, 0.5f)
        }

        // Observa evento de navegação para criação nova a partir do MapViewModel
        mapViewModel.navigationEvent.observe(viewLifecycleOwner) { novoArtefato ->
            novoArtefato?.let {
                // Seta no ArtifactViewModel (modo criação)
                artifactViewModel.setInitialCoordinates(it.quadra, it.xRelativo, it.yRelativo, it.sondagem)
                // Garante que artefato editável seja null para forçar modo criação
                // (nosso ArtifactViewModel já trata: se _artefatoEditavel == null -> criação)
                artifactViewModel.setArtifactToEdition(null)
                findNavController().navigate(R.id.action_MapArqueologicoFragment_to_ArtifactDetailFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
