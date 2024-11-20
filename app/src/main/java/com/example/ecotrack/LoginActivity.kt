package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ecotrack.R
import com.example.ecotrack.data.model.LoginRequest
import com.example.ecotrack.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var btnLogin: Button
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var possuiCadastro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        editEmail = findViewById(R.id.inputEmail)
        editSenha = findViewById(R.id.editTextTextPassword)
        btnLogin = findViewById(R.id.btnLogin)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        possuiCadastro = findViewById(R.id.possui_cadastro)
    }

    private fun setupClickListeners() {
        possuiCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            lifecycleScope.launch {
                realizarLogin()
            }
        }
    }

    private suspend fun realizarLogin() {
        try {
            val email = editEmail.text.toString()
            val senha = editSenha.text.toString()

            // Validações básicas
            if (email.isEmpty() || senha.isEmpty()) {
                showToast("Por favor, preencha todos os campos")
                return
            }

            // Validação básica de email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Por favor, insira um email válido")
                return
            }

            loadingProgressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false

            val loginRequest = LoginRequest(
                email = email,
                password = senha
            )

            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.login(loginRequest)
            }

            if (response.isSuccessful) {
                // Login bem sucedido
                withContext(Dispatchers.Main) {
                    val intent = Intent(this@LoginActivity, MenuActivity::class.java)
                    // Limpa a pilha de activities anteriores
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            } else {
                showToast("Erro no login: Verifique suas credenciais")
            }
        } catch (e: Exception) {
            showToast("Erro de conexão: ${e.message}")
        } finally {
            loadingProgressBar.visibility = View.GONE
            btnLogin.isEnabled = true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}