package com.example.projetocoliceu.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projetocoliceu.R
import com.example.projetocoliceu.databinding.ActivityMapBinding
import com.example.projetocoliceu.viewmodel.ArtifactViewModel

class MapaArqueologicoFragment : Fragment(R.layout.activity_map) {

    private var _binding: ActivityMapBinding? = null
    private val binding get() = _binding!!

    // ViewModel compartilhado entre Fragments
    private val viewModel: ArtifactViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchArtifacts()
            // 3. Inicialização e Observação
            setupObservers()
            setupMapInteractions()

            // Inicia o carregamento dos artefatos
            viewModel.fetchArtifacts()
        }

    private fun setupObservers() {
        // Observa a lista de artefatos para desenhar no mapa
        viewModel.artifacts.observe(viewLifecycleOwner) { lista ->
            // A função 'setArtefatos' deve ser implementada na sua Custom View 'mapaArqueologico'
            binding.mapaArqueologico.setArtefatos(lista)
        }

        // Opcional: Observa o estado de carregamento se houver um Spinner/ProgressBar
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    /**
     * Configura os listeners de interação do mapa (cliques nos pontos).
     */
    private fun setupMapInteractions() {
        // 4. Clique no Artefato (Ponto no Mapa) → Abre BottomSheet
        binding.mapaArqueologico.onArtefatoClick = { artefato ->

            // Crie uma instância da sua BottomSheet (classe não fornecida, assumida)
            ArtefatoOpcoesBottomSheet(
                artefato,

                // Ação de Editar
                onEditar = { a ->
                    // 4a. Injeta o artefato no ViewModel (modo edição)
                    viewModel.setArtifactToEdition(a)

                    // 4b. Navega para a tela de edição
                    findNavController().navigate(
                        R.id.action_MapArqueologicoFragment_to_ArtifactDetailFragment
                    )
                },

                // Ação de Excluir
                onExcluir = { a ->
                    // 4c. Chama a função de exclusão no ViewModel
                    viewModel.deleteArtifact(a)
                    Toast.makeText(requireContext(),
                        getString(R.string.artifact_deleted, a.id),
                        Toast.LENGTH_SHORT).show()
                }
            ).show(parentFragmentManager, "OpcoesArtefato")
        }

        // Opcional: Adicionar listener para criar um novo artefato (clique longo ou botão)
        binding.mapaArqueologico.onMapLongPress = { quadra, x, y ->
            // 5. Inicia o fluxo de criação
            handleNewArtifactCreation(quadra, x, y)
        }
    }

    /**
     * Prepara o ViewModel para a criação de um novo artefato e navega.
     */
    private fun handleNewArtifactCreation(quadra: String, x: Float, y: Float) {
        // 1. Limpa o estado de edição (se houver)
        viewModel.clearEditionState() // É importante adicionar esta função ao ViewModel

        // 2. Injeta as coordenadas iniciais
        viewModel.setInitialCoordinates(quadra, x, y)

        // 3. Navega para a tela de detalhes/formulário
        findNavController().navigate(
            R.id.action_MapArqueologicoFragment_to_ArtifactDetailFragment
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
