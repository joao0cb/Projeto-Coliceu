package com.example.projetocoliceu.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projetocoliceu.R
import com.google.android.material.button.MaterialButton
import android.widget.TextView

class loginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnEntrar = view.findViewById<MaterialButton>(R.id.btnEntrar)
        val tvIrParaCadastro = view.findViewById<TextView>(R.id.tvIrParaCadastro)

        tvIrParaCadastro.setOnClickListener {
            findNavController().navigate(R.id.cadastroUsuarioFragment)
        }

        btnEntrar.setOnClickListener {
            findNavController().navigate(R.id.MapArqueologicoFragment)
        }
    }
}