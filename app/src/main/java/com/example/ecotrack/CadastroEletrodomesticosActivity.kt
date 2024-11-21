package com.example.ecotrack

import Appliance
import UserApplianceRequest
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ecotrack.R
import com.example.ecotrack.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CadastroEletrodomesticosActivity : AppCompatActivity() {
    private lateinit var spinnerEletrodomestico: Spinner
    private lateinit var editHorasUsadas: EditText
    private lateinit var editVezesUsado: EditText
    private lateinit var btnCadastrar: Button
    private var appliances: List<Appliance> = emptyList()

    companion object {
        private const val TAG = "CadastroEletro"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro_eletrodomesticos)
        Log.d(TAG, "Activity Created")

        initViews()
        loadAppliances()
        setupListeners()
    }

    private fun initViews() {
        Log.d(TAG, "Initializing views")
        spinnerEletrodomestico = findViewById(R.id.spinnerEletrodomestico)
        editHorasUsadas = findViewById(R.id.editHorasUsadas)
        editVezesUsado = findViewById(R.id.editVezesUsado)
        btnCadastrar = findViewById(R.id.btnCadastrar)

        findViewById<ImageButton>(R.id.btnVol).setOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        Log.d(TAG, "Setting up listeners")
        btnCadastrar.setOnClickListener {
            Log.d(TAG, "Cadastrar button clicked")
            lifecycleScope.launch {
                cadastrarEletrodomestico()
            }
        }
    }

    private fun loadAppliances() {
        Log.d(TAG, "Starting loadAppliances")
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Making API request")
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getAppliances()
                }

                Log.d(TAG, "API Response - Success: ${response.isSuccessful}, Code: ${response.code()}")

                if (response.isSuccessful) {
                    response.body()?.let { appliancesList ->
                        Log.d(TAG, "Received appliances: ${appliancesList.size}")
                        appliances = appliancesList
                        val applianceNames = appliancesList.map {
                            Log.d(TAG, "Appliance: ${it.name}, ID: ${it.id}")
                            it.name
                        }
                        setupSpinner(applianceNames)
                    } ?: run {
                        Log.e(TAG, "Response body is null")
                        showToast("Erro: Lista de eletrodomésticos vazia")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Error response: $errorBody")
                    showToast("Erro ao carregar eletrodomésticos: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in loadAppliances", e)
                showToast("Erro de conexão: ${e.message}")
            }
        }
    }

    private fun setupSpinner(applianceNames: List<String>) {
        Log.d(TAG, "Setting up spinner with ${applianceNames.size} items")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            applianceNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEletrodomestico.adapter = adapter
    }

    private suspend fun cadastrarEletrodomestico() {
        try {
            Log.d(TAG, "Starting cadastrarEletrodomestico")
            val horasUsadas = editHorasUsadas.text.toString().toDoubleOrNull()
            val vezesUsado = editVezesUsado.text.toString().toIntOrNull()

            Log.d(TAG, "Input values - Hours: $horasUsadas, Days: $vezesUsado")

            if (horasUsadas == null || vezesUsado == null) {
                Log.e(TAG, "Invalid input values")
                showToast("Por favor, preencha todos os campos corretamente")
                return
            }

            if (horasUsadas > 24) {
                Log.e(TAG, "Hours exceed limit: $horasUsadas")
                showToast("O número de horas não pode ser maior que 24")
                return
            }

            if (vezesUsado > 7) {
                Log.e(TAG, "Days exceed limit: $vezesUsado")
                showToast("O número de dias não pode ser maior que 7")
                return
            }

            val selectedPosition = spinnerEletrodomestico.selectedItemPosition
            val selectedAppliance = appliances[selectedPosition]

            Log.d(TAG, "Selected appliance - Position: $selectedPosition, ID: ${selectedAppliance.id}")

            val request = UserApplianceRequest(
                appliance_id = selectedAppliance.id,
                minutesUsedPerDay = editHorasUsadas.text.toString().toInt(), // Enviando minutos diretamente
                daysUsedPerWeek = vezesUsado
            )

            Log.d(TAG, "Sending request: $request")

            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.registerUserAppliance(request)
            }

            Log.d(TAG, "Register response - Success: ${response.isSuccessful}")

            if (response.isSuccessful) {
                showToast("Eletrodoméstico cadastrado com sucesso!")
                finish()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Error registering appliance: $errorBody")
                showToast("Erro ao cadastrar eletrodoméstico: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in cadastrarEletrodomestico", e)
            showToast("Erro: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        Log.d(TAG, "Showing toast: $message")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}