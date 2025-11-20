package com.example.projetocoliceu.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projetocoliceu.databinding.BottomsheetArtifactOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.projetocoliceu.data.model.Artefato

class ArtefatoOpcoesBottomSheet(
    private val artefato: Artefato,
    private val onEditar: (Artefato) -> Unit,
    private val onExcluir: (Artefato) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomsheetArtifactOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetArtifactOptionsBinding.inflate(inflater, container, false)

        binding.btnAtualizar.setOnClickListener {
            onEditar(artefato)
            dismiss()
        }

        binding.btnExcluir.setOnClickListener {
            onExcluir(artefato)
            dismiss()
        }

        binding.btnCancelar.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}
