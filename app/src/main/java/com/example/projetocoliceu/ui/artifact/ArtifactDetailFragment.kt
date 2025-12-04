package com.example.projetocoliceu.ui.artifact

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projetocoliceu.data.api.RetrofitClient
import com.example.projetocoliceu.data.db.AppDatabase
import com.example.projetocoliceu.data.repository.ArtefatoRepository
import com.example.projetocoliceu.databinding.FragmentArtifactBinding
import com.example.projetocoliceu.viewmodel.ArtifactViewModel
import com.example.projetocoliceu.viewmodel.ArtifactViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import androidx.navigation.fragment.navArgs
import com.example.projetocoliceu.data.repository.MapRepository
import com.example.projetocoliceu.viewmodel.MapViewModel
import com.example.projetocoliceu.viewmodel.MapViewModelFactory

class ArtifactDetailFragment : Fragment() {

    private val args: ArtifactDetailFragmentArgs by navArgs()

    // 🔥 MUDANÇA: Usar activityViewModels para compartilhar o ViewModel
    private val viewModel: ArtifactViewModel by activityViewModels {
        ArtifactViewModelFactory(
            ArtefatoRepository(
                apiService = RetrofitClient.apiService,
                dao = AppDatabase.getDatabase(requireContext()).artefatoDao(),
                context = requireContext()
            )
        )
    }

    private val mapViewModel: MapViewModel by activityViewModels {
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

    private var _binding: FragmentArtifactBinding? = null
    private val binding get() = _binding!!

    // campos
    private lateinit var etNome: TextInputEditText
    private lateinit var etArea: TextInputEditText
    private lateinit var etQuadra: TextInputEditText
    private lateinit var etSondagem: TextInputEditText
    private lateinit var etGps: TextInputEditText
    private lateinit var etNivel: TextInputEditText
    private lateinit var etCamada: TextInputEditText
    private lateinit var etDecapagem: TextInputEditText
    private lateinit var etMaterial: TextInputEditText
    private lateinit var etQuantidade: TextInputEditText
    private lateinit var etData: TextInputEditText
    private lateinit var etPesquisador: TextInputEditText
    private lateinit var etObs: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentArtifactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔥 Aguarda o ViewModel estar pronto
        // O mapId JÁ FOI SETADO no MapaArqueologicoFragment via activityViewModels
        Log.d("ArtifactDetailFragment", "Iniciando fragment...")

        // Inicializa campos a partir do binding
        etNome = binding.etNome
        etArea = binding.etArea
        etQuadra = binding.etQuadra
        etSondagem = binding.etSondagem
        etGps = binding.etGps
        etNivel = binding.etNivel
        etCamada = binding.etCamada
        etDecapagem = binding.etDecapagem
        etMaterial = binding.etMaterial
        etQuantidade = binding.etQuantidade
        etData = binding.etData
        etPesquisador = binding.etPesquisador
        etObs = binding.etObs

        val btnSalvar = binding.btnAdicionar
        val btnDeletar = binding.btnDeletar
        val tvQuadraInfo = binding.tvQuadraInfo

        // Observadores para preencher UI
        setupFormObservers(tvQuadraInfo)

        // Salvar/Atualizar
        btnSalvar.setOnClickListener {
            transferFormToViewModel()

            val currentMapId = viewModel.mapId.value
            Log.d("ArtifactDetailFragment", "Tentando salvar. MapId: $currentMapId")

            if (currentMapId.isNullOrEmpty()) {
                Snackbar.make(binding.root, "Erro: nenhum mapa selecionado!", Snackbar.LENGTH_LONG).show()
                Log.e("ArtifactDetailFragment", "MapId está vazio!")
            } else {
                Log.d("ArtifactDetailFragment", "Salvando artefato no mapa: $currentMapId")
                viewModel.saveArtifact()
            }
        }

        // Deletar
        btnDeletar.setOnClickListener {
            val atual = viewModel.artefatoEditavel.value
            if (atual != null) {
                viewModel.deleteArtifact(atual)
                findNavController().popBackStack()
            } else {
                findNavController().popBackStack()
            }
        }

        binding.btnFoto.setOnClickListener {
            abrirGaleria()
        }

        binding.btnVoltar.setOnClickListener{
            findNavController().navigateUp()
        }

        // Observa sucesso do salvamento
        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                val currentMapId = viewModel.mapId.value
                Log.d("ArtifactDetailFragment", "Artefato salvo com sucesso! MapId: $currentMapId")

                // Atualiza lista de artefatos do mapa
                if (!currentMapId.isNullOrEmpty()) {
                    mapViewModel.getArtifactsByMap(currentMapId)
                }

                // Limpa edição e volta
                viewModel.clearEditionIfNeeded()
                findNavController().popBackStack()
            }
        }

        viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                viewModel.clearEditionIfNeeded()
                findNavController().popBackStack()
            }
        }
    }

    private fun setupFormObservers(tvQuadraInfo: TextView) {
        viewModel.initialQuadra.observe(viewLifecycleOwner) { quadra ->
            val x = viewModel.initialXRelativo.value ?: 0f
            val y = viewModel.initialYRelativo.value ?: 0f
            val sondagem = viewModel.initialSondagem.value ?: "N/A"
            tvQuadraInfo.text = "Quadra: $quadra, Sondagem: $sondagem (x=${"%.2f".format(x)}, y=${"%.2f".format(y)})"

            if (viewModel.artefatoEditavel.value == null) {
                etQuadra.setText(quadra)
            }
        }

        viewModel.artefatoEditavel.observe(viewLifecycleOwner) { artefato ->
            artefato?.let {
                etNome.setText(it.nome)
                etArea.setText(it.area)
                etQuadra.setText(it.quadra)
                etSondagem.setText(it.sondagem)
                etGps.setText(it.pontoGPS)
                etNivel.setText(it.nivel)
                etCamada.setText(it.camada)
                etDecapagem.setText(it.decapagem)
                etMaterial.setText(it.material)
                etQuantidade.setText(it.quantidade.toString())
                etData.setText(it.data)
                etPesquisador.setText(it.pesquisador)
                etObs.setText(it.obs)
            }
        }
    }

    private fun transferFormToViewModel() {
        viewModel.nome.value = etNome.text?.toString() ?: ""
        viewModel.area.value = etArea.text?.toString() ?: ""
        viewModel.quadra.value = etQuadra.text?.toString() ?: ""
        viewModel.sondagem.value = etSondagem.text?.toString() ?: ""
        viewModel.pontoGPS.value = etGps.text?.toString()?.takeIf { it.isNotBlank() }

        viewModel.nivel.value = etNivel.text?.toString()?.toIntOrNull()
        viewModel.camada.value = etCamada.text?.toString() ?: ""
        viewModel.decapagem.value = etDecapagem.text?.toString()?.takeIf { it.isNotBlank() }

        viewModel.material.value = etMaterial.text?.toString() ?: ""
        viewModel.quantidade.value = etQuantidade.text?.toString()?.toIntOrNull() ?: 1

        viewModel.data.value = etData.text?.toString()?.takeIf { it.isNotBlank() }
        viewModel.pesquisador.value = etPesquisador.text?.toString()?.takeIf { it.isNotBlank() }
        viewModel.obs.value = etObs.text?.toString()?.takeIf { it.isNotBlank() }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            binding.imgArtefato.setImageURI(it)
            viewModel.fotoCaminho.value = it.toString()
        }
    }

    private fun abrirGaleria() {
        pickImageLauncher.launch("image/*")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}