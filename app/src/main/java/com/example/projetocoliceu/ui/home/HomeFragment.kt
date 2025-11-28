package com.example.projetocoliceu.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projetocoliceu.R
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnLogin = view.findViewById<MaterialButton>(R.id.btnLogin)
        val btnCadastro = view.findViewById<MaterialButton>(R.id.btnCadastro)

        btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_login)
        }

        btnCadastro.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_cadastro)
        }
    }
}
