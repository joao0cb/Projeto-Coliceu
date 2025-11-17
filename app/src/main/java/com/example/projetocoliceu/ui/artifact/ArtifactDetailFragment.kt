// RegistroArtefatoFragment.kt
package com.example.projetocoliceu.ui.registro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // Importe para usar o ViewModel Compartilhado
import androidx.navigation.fragment.findNavController // Se estiver usando o Navigation Component
import com.example.projetocoliceu.R
import com.example.projetocoliceu.viewmodel.ArtifactViewModel
import com.google.android.material.textfield.TextInputEditText

class RegistroArtefatoFragment : Fragment() {

    // 1. Acesso ao ViewModel Compartilhado
    // Certifique-se de que a Activity hospedeira ou o grafo de navegação defina o escopo corretamente
    private val viewModel: ArtifactViewModel by activityViewModels()

    // Referência aos campos de entrada do layout (necessário para coletar dados)
    private lateinit var etArea: TextInputEditText
    private lateinit var etMaterial: TextInputEditText
    private lateinit var etObs: TextInputEditText
    // ... adicione referências para todos os outros campos

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout fragment_registro_artefato.xml
        val view = inflater.inflate(R.layout.fragment_register_artifact, container, false)

        // 2. Inicializa as referências dos campos do formulário
        etArea = view.findViewById(R.id.et_area)
        etMaterial = view.findViewById(R.id.til_material) // Corrigir o ID se necessário
        etObs = view.findViewById(R.id.et_obs)

        val btnSalvar = view.findViewById<Button>(R.id.btn_salvar)
        val tvQuadraInfo = view.findViewById<TextView>(R.id.tv_quadra_info)

        // 3. Preencher a interface e os LiveDatas do formulário se estiver editando ou criando
        setupFormObservers(tvQuadraInfo)

        // 4. Configurar o botão Salvar
        btnSalvar.setOnClickListener {
            // Garante que os dados do formulário sejam transferidos para os LiveDatas do ViewModel
            transferFormToViewModel()

            // Chama a lógica de salvar/atualizar
            viewModel.saveOrUpdateArtifact()
        }

        // 5. Observar o sucesso do salvamento para fechar a tela
        viewModel.saveSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                // Navega de volta ao mapa (assumindo que você usa o Navigation Component)
                findNavController().popBackStack()
            }
        }

        return view
    }

    private fun setupFormObservers(tvQuadraInfo: TextView) {
        // Preenche o campo de Coordenadas do Mapa (Ação 3)
        viewModel.initialQuadra.observe(viewLifecycleOwner) { quadra ->
            val x = viewModel.initialXRelativo.value ?: 0f
            val y = viewModel.initialYRelativo.value ?: 0f

            val quadraInfo = "Quadra: $quadra (x=${"%.2f".format(x)}, y=${"%.2f".format(y)})"
            tvQuadraInfo.text = quadraInfo
        }

        // Se estiver editando, preenche os campos do formulário com os dados do artefato editável
        viewModel.artefatoEditavel.observe(viewLifecycleOwner) { artefato ->
            artefato?.let {
                etArea.setText(it.area)
                etMaterial.setText(it.material)
                etObs.setText(it.obs)
                // ... preencher todos os campos restantes
            }
        }
    }

    // Transfere o valor atual dos campos de texto (EditText) para os LiveDatas do ViewModel
    private fun transferFormToViewModel() {
        viewModel.area.value = etArea.text.toString()
        viewModel.tipo.value = etMaterial.text.toString()
        viewModel.obs.value = etObs.text.toString()
        // ... Transferir todos os outros campos
    }
}