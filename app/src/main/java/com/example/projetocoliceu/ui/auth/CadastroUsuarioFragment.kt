package com.example.projetocoliceu.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.projetocoliceu.R
import com.example.projetocoliceu.viewmodel.CadastroUsuarioViewModel

class CadastroUsuarioFragment : Fragment() {

    // Usa o ViewModel criado acima
    private val viewModel: CadastroUsuarioViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Crie um layout XML chamado 'fragment_cadastro_usuario'
        val view = inflater.inflate(R.layout.fragment_cadastro_usuario, container, false)

        val etNome = view.findViewById<EditText>(R.id.et_nome_cadastro)
        val etEmail = view.findViewById<EditText>(R.id.et_email_cadastro)
        val etSenha = view.findViewById<EditText>(R.id.et_senha_cadastro)
        val btnCadastrar = view.findViewById<Button>(R.id.btn_realizar_cadastro)
        val loading = view.findViewById<ProgressBar>(R.id.progressBarCadastro)

        // 1. Ação do Botão
        btnCadastrar.setOnClickListener {
            val nome = etNome.text.toString()
            val email = etEmail.text.toString()
            val senha = etSenha.text.toString()

            if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
                Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.cadastrarUsuario(nome, email, senha)
        }


        // 2. Observar Sucesso
        viewModel.cadastroSucesso.observe(viewLifecycleOwner) { sucesso ->
            if (sucesso) {
                Toast.makeText(context, "Usuário criado com sucesso!", Toast.LENGTH_SHORT).show()
                // Navegar para a tela de Login ou Mapa
                findNavController().navigate(R.id.action_cadastro_to_login)
            }
        }

        // 3. Observar Erro
        viewModel.mensagemErro.observe(viewLifecycleOwner) { erro ->
            Toast.makeText(context, erro, Toast.LENGTH_LONG).show()
        }

        // 4. Observar Loading (opcional, para UX)
        viewModel.isLoading.observe(viewLifecycleOwner) { carregando ->
            loading.visibility = if (carregando) View.VISIBLE else View.GONE
            btnCadastrar.isEnabled = !carregando
        }

        return view
    }
}