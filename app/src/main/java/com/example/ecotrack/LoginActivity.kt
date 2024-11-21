package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

    companion object {
        private const val TAG = "LoginActivity"
    }

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

            if (email.isEmpty() || senha.isEmpty()) {
                showToast("Por favor, preencha todos os campos")
                return
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Por favor, insira um email válido")
                return
            }

            loadingProgressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false

            val loginRequest = LoginRequest(email = email, password = senha)

            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.login(loginRequest)
            }

            if (response.isSuccessful) {
                val loginResponse = response.body()
                Log.d(TAG, "Response body: $loginResponse")

                if (loginResponse != null) {
                    val token = loginResponse.token
                    Log.d(TAG, "Token extraído: $token")

                    if (token.isNotEmpty()) {
                        RetrofitClient.setToken(token)

                        withContext(Dispatchers.Main) {
                            val intent = Intent(this@LoginActivity, MenuActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        showToast("Erro: Token vazio na resposta")
                    }
                } else {
                    showToast("Erro: Resposta vazia do servidor")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Erro no login: $errorBody")
                showToast("Erro no login: Verifique suas credenciais")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro durante login", e)
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