package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.ecotrack.data.remote.RetrofitClient
import com.example.ecotrack.interfaces.ConsumptionUpdateListener
import com.example.ecotrack.view.ConsumptionMeterView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuActivity : AppCompatActivity(), ConsumptionUpdateListener {
    private lateinit var consumptionMeter: ConsumptionMeterView

    companion object {
        private var instance: MenuActivity? = null
        fun getInstance(): MenuActivity? = instance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        instance = this

        initViews()
        loadConsumption()
    }

    private fun initViews() {
        consumptionMeter = findViewById(R.id.consumptionMeter)

        findViewById<CardView>(R.id.cardConsumo).setOnClickListener {
            val intent = Intent(this, MeuConsumoActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.btnCadastrar).setOnClickListener {
            val intent = Intent(this, CadastroEletrodomesticosActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.btnRelatorios).setOnClickListener {
            val intent = Intent(this, RelatorioConsumoActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.btnComoUsar).setOnClickListener {
            val intent = Intent(this, ComoUsarActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.btnDicas).setOnClickListener {
            val intent = Intent(this, DicasActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadConsumption() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getConsumptionReport()
                }

                if (response.isSuccessful && response.body() != null) {
                    val report = response.body()!!
                    consumptionMeter.setConsumption(report.totalConsumption.toFloat())
                }
            } catch (e: Exception) {
                consumptionMeter.setConsumption(0f)
                Toast.makeText(
                    this@MenuActivity,
                    "Erro ao carregar consumo: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onConsumptionUpdated() {
        loadConsumption()
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}