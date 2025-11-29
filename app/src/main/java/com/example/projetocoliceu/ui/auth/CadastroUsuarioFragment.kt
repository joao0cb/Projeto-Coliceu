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
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.ViewModelProvider // Novo Import
import com.example.projetocoliceu.R
import com.example.projetocoliceu.data.db.AppDatabase // Novo Import
import com.example.projetocoliceu.data.repository.UserRepository // Novo Import
import com.example.projetocoliceu.viewmodel.CadastroUsuarioViewModel
import com.example.projetocoliceu.viewmodel.UserViewModelFactory // Novo Import

class CadastroUsuarioFragment : Fragment() {

    // 1. O ViewModel será inicializado após a View ser criada
    private lateinit var viewModel: CadastroUsuarioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cadastro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 2. INJEÇÃO MANUAL DE DEPENDÊNCIA (CORREÇÃO DO ERRO) ---

        // a. Obter o Contexto da Aplicação (necessário para o Room)
        val appContext = requireContext().applicationContext

        // b. Obter o DAO a partir do Banco de Dados
        val userDao = AppDatabase.getDatabase(appContext).userDao()

        // c. Criar o Repositório, passando o DAO
        val userRepository = UserRepository(userDao)

        // d. Criar a Factory, passando o Repositório
        val factory = UserViewModelFactory(userRepository)

        // e. Obter/Instanciar o ViewModel usando a Factory
        viewModel = ViewModelProvider(
            this, // Usa o Fragment como dono do ciclo de vida
            factory
        ).get(CadastroUsuarioViewModel::class.java)

        // -------------------------------------------------------------------


        // Referências à UI
        val etNome = view.findViewById<EditText>(R.id.et_nome_cadastro)
        val etEmail = view.findViewById<EditText>(R.id.et_email_cadastro)
        val etSenha = view.findViewById<EditText>(R.id.et_senha_cadastro)
        val btnCadastrar = view.findViewById<Button>(R.id.btn_realizar_cadastro)
        val loading = view.findViewById<ProgressBar>(R.id.progressBarCadastro)

        // 3. Ação do Botão
        btnCadastrar.setOnClickListener {
            val nome = etNome.text.toString()
            val email = etEmail.text.toString()
            val senha = etSenha.text.toString()

            if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
                Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // A chamada ao ViewModel está correta
            viewModel.cadastrarUsuario(nome, email, senha)
        }


        // 4. Observar Sucesso
        viewModel.cadastroSucesso.observe(viewLifecycleOwner) { sucesso ->
            if (sucesso) {
                Toast.makeText(context, "Usuário criado com sucesso!", Toast.LENGTH_SHORT).show()
                // Navegar para a tela de Login ou Mapa
                findNavController().navigate(R.id.action_cadastro_to_login)
            }
        }

        // 5. Observar Erro
        viewModel.mensagemErro.observe(viewLifecycleOwner) { erro ->
            Toast.makeText(context, erro, Toast.LENGTH_LONG).show()
        }

        // 6. Observar Loading
        viewModel.isLoading.observe(viewLifecycleOwner) { carregando ->
            loading.visibility = if (carregando) View.VISIBLE else View.GONE
            btnCadastrar.isEnabled = !carregando
        }
    }
}