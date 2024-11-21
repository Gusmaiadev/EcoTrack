package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ecotrack.data.model.SignupRequest
import com.example.ecotrack.data.model.State
import com.example.ecotrack.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CadastroActivity : AppCompatActivity() {
    private lateinit var editNome: EditText
    private lateinit var editDataNascimento: EditText
    private lateinit var spinnerEstado: Spinner
    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var btnCadastrar: Button
    private lateinit var loadingProgressBar: ProgressBar

    private var states: List<State> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        initViews()
        setupClickListeners()
        loadStates()
    }

    private fun initViews() {
        editNome = findViewById(R.id.editNome)
        editDataNascimento = findViewById(R.id.editDataNascimento)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        editEmail = findViewById(R.id.editEmail)
        editSenha = findViewById(R.id.editSenha)
        btnCadastrar = findViewById(R.id.btnCadastrar)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        // Handler do botão de login
        findViewById<TextView>(R.id.possui_conta).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupClickListeners() {
        btnCadastrar = findViewById(R.id.btnCadastrar)
        btnCadastrar.setOnClickListener {
            lifecycleScope.launch {
                realizarCadastro()
            }
        }
    }

    private fun loadStates() {
        lifecycleScope.launch {
            try {
                loadingProgressBar.visibility = View.VISIBLE
                spinnerEstado.visibility = View.GONE

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getStates()
                }

                if (response.isSuccessful) {
                    response.body()?.let { statesList ->
                        states = statesList
                        setupSpinner(statesList)
                    }
                } else {
                    showToast("Erro ao carregar estados: ${response.message()}")
                }
            } catch (e: Exception) {
                showToast("Erro de conexão: ${e.message}")
            } finally {
                loadingProgressBar.visibility = View.GONE
                spinnerEstado.visibility = View.VISIBLE
            }
        }
    }

    private fun setupSpinner(states: List<State>) {
        // Cria uma lista apenas com os nomes dos estados
        val stateNames = states.map { it.name }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            stateNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapter
    }

    private suspend fun realizarCadastro() {
        try {
            val nome = editNome.text.toString().trim()
            val dataNascimento = editDataNascimento.text.toString().trim()
            val estadoSelecionadoNome = spinnerEstado.selectedItem?.toString()
            val estadoSelecionado = states.find { it.name == estadoSelecionadoNome }?.abbreviation
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString().trim()

            // Validações básicas
            if (nome.isEmpty() || dataNascimento.isEmpty() || estadoSelecionado == null ||
                email.isEmpty() || senha.isEmpty()) {
                showToast("Por favor, preencha todos os campos")
                return
            }

            // Validação básica de email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Por favor, insira um email válido")
                return
            }

            // Validação de formato da data
            if (!dataNascimento.matches("\\d{4}-\\d{2}-\\d{2}".toRegex())) {
                showToast("Por favor, insira a data no formato AAAA-MM-DD")
                return
            }

            loadingProgressBar.visibility = View.VISIBLE
            btnCadastrar.isEnabled = false

            val signupRequest = SignupRequest(
                name = nome,
                birthDate = dataNascimento,
                abbreviationState = estadoSelecionado,
                email = email,
                password = senha
            )

            withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitClient.apiService.signup(signupRequest)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            showToast("Cadastro realizado com sucesso!")
                            val intent = Intent(this@CadastroActivity, CadastroSucessoActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            showToast("Erro no cadastro: $errorBody")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showToast("Erro de conexão: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            showToast("Erro: ${e.message}")
        } finally {
            loadingProgressBar.visibility = View.GONE
            btnCadastrar.isEnabled = true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}