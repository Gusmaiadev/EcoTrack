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
        loadingProgressBar = findViewById(R.id.loadingProgressBar) // Você precisará adicionar este ProgressBar no seu layout
    }

    private fun setupClickListeners() {
        findViewById<TextView>(R.id.possui_conta).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

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
        val stateAbbreviations = states.map { it.abbreviationState }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            stateAbbreviations
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapter
    }

    private suspend fun realizarCadastro() {
        try {
            val nome = editNome.text.toString()
            val dataNascimento = editDataNascimento.text.toString()
            val estadoSelecionado = spinnerEstado.selectedItem?.toString()
            val email = editEmail.text.toString()
            val senha = editSenha.text.toString()

            // Validações básicas
            if (nome.isEmpty() || dataNascimento.isEmpty() || estadoSelecionado == null ||
                email.isEmpty() || senha.isEmpty()) {
                showToast("Por favor, preencha todos os campos")
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

            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.signup(signupRequest)
            }

            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    showToast("Cadastro realizado com sucesso!")
                    val intent = Intent(this@CadastroActivity, CadastroSucessoActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                showToast("Erro no cadastro: ${response.message()}")
            }
        } catch (e: Exception) {
            showToast("Erro de conexão: ${e.message}")
        } finally {
            loadingProgressBar.visibility = View.GONE
            btnCadastrar.isEnabled = true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}