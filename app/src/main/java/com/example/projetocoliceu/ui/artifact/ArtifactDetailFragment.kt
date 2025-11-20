package com.example.projetocoliceu.ui.artifact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projetocoliceu.R
import com.example.projetocoliceu.data.model.Artefato
import com.example.projetocoliceu.viewmodel.ArtifactViewModel
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale

// Assumindo que este Fragment usa o layout "ScrollView" fornecido anteriormente.
class ArtifactDetailFragment : Fragment() {

    // Acesso ao ViewModel Compartilhado (escopo da Activity)
    private val viewModel: ArtifactViewModel by activityViewModels()

    // Referências a TODOS os campos de entrada do layout (et_*)
    private lateinit var etArea: TextInputEditText
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
        savedInstanceState: Bundle?
    ): View? {
        // Assume-se que R.layout.fragment_register_artifact é o seu layout de formulário (ScrollView)
        val view = inflater.inflate(R.layout.fragment_register_artifact, container, false)

        // 2. Inicializa as referências dos campos do formulário (Usando IDs Corretos)
        etArea = view.findViewById(R.id.et_area)
        etSondagem = view.findViewById(R.id.et_sondagem)
        etGps = view.findViewById(R.id.et_gps)
        etNivel = view.findViewById(R.id.et_nivel)
        etCamada = view.findViewById(R.id.et_camada)
        etDecapagem = view.findViewById(R.id.et_decapagem)
        etMaterial = view.findViewById(R.id.et_material)
        etQuantidade = view.findViewById(R.id.et_quantidade)
        etData = view.findViewById(R.id.et_data)
        etPesquisador = view.findViewById(R.id.et_pesquisador)
        etObs = view.findViewById(R.id.et_obs)

        val btnSalvar = view.findViewById<Button>(R.id.btn_salvar)
        val tvQuadraInfo = view.findViewById<TextView>(R.id.tv_quadra_info)

        // 3. Preencher a interface e os LiveDatas do formulário
        setupFormObservers(tvQuadraInfo)

        // 4. Configurar o botão Salvar
        btnSalvar.setOnClickListener {
            // 🚨 Etapa Crítica: Transferir os dados da View (EditText) para o ViewModel (LiveData)
            transferFormToViewModel()

            viewModel.saveOrUpdateArtifact()
        }

        // 5. Observar o sucesso do salvamento para fechar a tela
        viewModel.saveSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) { // Use '== true' para tratar o Boolean? de LiveData
                // Navega de volta, assumindo que este fragmento foi aberto a partir do mapa
                findNavController().popBackStack()
            }
        }

        return view
    }

    /**
     * Observa LiveDatas do ViewModel para preencher a UI.
     */
    private fun setupFormObservers(tvQuadraInfo: TextView) {

        // Exibe as coordenadas iniciais fixadas
        viewModel.initialQuadra.observe(viewLifecycleOwner) { quadra ->
            val x = viewModel.initialXRelativo.value ?: 0f
            val y = viewModel.initialYRelativo.value ?: 0f
            val sondagem = viewModel.initialSondagem.value ?: "N/A"

            // Atualiza o TextView informativo no topo
            val quadraInfo = "Quadra: $quadra, Sondagem: $sondagem (x=${"%.2f".format(x)}, y=${"%.2f".format(y)})"
            tvQuadraInfo.text = quadraInfo
        }

        // Observa o artefato de edição para pré-preencher todos os campos
        viewModel.artefatoEditavel.observe(viewLifecycleOwner) { artefato ->
            artefato?.let {
                // Preenche campos de Localização/Contexto
                etArea.setText(it.area)
                etSondagem.setText(it.sondagem)
                etGps.setText(it.pontoGPS)

                etNivel.setText(it.nivel) // Nível no VM é Int, no model é String. Precisa ser String aqui.
                etCamada.setText(it.camada)
                etDecapagem.setText(it.decapagem)

                // Preenche Detalhes
                etMaterial.setText(it.material)
                etQuantidade.setText(it.quantidade.toString())

                // Preenche Logística
                etData.setText(it.data)
                etPesquisador.setText(it.pesquisador)
                etObs.setText(it.obs)
            }
        }
    }

    /**
     * Transfere o valor atual dos campos de texto (EditText) para os LiveDatas do ViewModel,
     * garantindo que o ViewModel tenha os dados mais recentes antes de salvar.
     */
    private fun transferFormToViewModel() {
        // Coordenadas/Localização
        viewModel.area.value = etArea.text.toString()
        viewModel.sondagem.value = etSondagem.text.toString()
        viewModel.pontoGPS.value = etGps.text.toString().takeIf { it.isNotBlank() } // Envia null se vazio

        // Contexto Estratigráfico
        viewModel.nivel.value = etNivel.text.toString().toIntOrNull()
        viewModel.camada.value = etCamada.text.toString()
        viewModel.decapagem.value = etDecapagem.text.toString().takeIf { it.isNotBlank() }

        // Detalhes do Achado
        viewModel.material.value = etMaterial.text.toString()
        viewModel.quantidade.value = etQuantidade.text.toString().toIntOrNull()

        // Logística e Observações
        viewModel.data.value = etData.text.toString().takeIf { it.isNotBlank() }
        viewModel.pesquisador.value = etPesquisador.text.toString().takeIf { it.isNotBlank() }
        viewModel.obs.value = etObs.text.toString().takeIf { it.isNotBlank() }

        // Não estamos mexendo com fotoCaminho, pois isso viria de uma ação da câmera (btn_foto)
    }
}