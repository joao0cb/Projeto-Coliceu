package com.example.projetocoliceu.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText // Import Adicionado
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider // Import Necessário
import androidx.navigation.fragment.findNavController
import com.example.projetocoliceu.R
import com.example.projetocoliceu.data.db.AppDatabase // Import do Room
import com.example.projetocoliceu.data.repository.UserRepository
import com.example.projetocoliceu.viewmodel.LoginViewModel // Novo Import
import com.example.projetocoliceu.viewmodel.UserViewModelFactory // Import da Factory
import com.google.android.material.button.MaterialButton
import android.widget.TextView

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 1. INSTANCIAÇÃO DO VIEWMODEL (usando a Factory) ---
        val appContext = requireContext().applicationContext
        val userDao = AppDatabase.getDatabase(appContext).userDao()
        val userRepository = UserRepository(userDao)
        val factory = UserViewModelFactory(userRepository) // Reutiliza a Factory

        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        // -------------------------------------------------------

        // Assumindo os IDs para os campos de texto:
        val etEmail = view.findViewById<EditText>(R.id.etEmailLogin)
        val etSenha = view.findViewById<EditText>(R.id.etSenhaLogin)

        val btnEntrar = view.findViewById<MaterialButton>(R.id.btnEntrar)
        val tvIrParaCadastro = view.findViewById<TextView>(R.id.tvIrParaCadastro)

        tvIrParaCadastro.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_cadastro)
        }

        // --- 2. LÓGICA DE CLIQUE: CHAMA O VIEWMODEL ---
        btnEntrar.setOnClickListener {
            val email = etEmail.text.toString()
            val senha = etSenha.text.toString()

            // Chama a função de login. A navegação será feita no observador.
            viewModel.fazerLogin(email, senha)
        }

        // --- 3. OBSERVAR O STATUS DO LOGIN ---
        viewModel.loginSucesso.observe(viewLifecycleOwner) { sucesso ->
            if (sucesso) {
                Toast.makeText(context, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                // A navegação só ocorre SE o Room confirmar as credenciais
                findNavController().navigate(R.id.action_login_to_mapa)
            }
        }

        viewModel.mensagemErro.observe(viewLifecycleOwner) { erro ->
            if (!erro.isNullOrEmpty()) {
                Toast.makeText(context, erro, Toast.LENGTH_LONG).show()
            }
        }
    }
}