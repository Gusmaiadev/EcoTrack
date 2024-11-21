package com.example.ecotrack

import ApplianceReport
import ConsumptionReport
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.ecotrack.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MeuConsumoActivity : AppCompatActivity() {
    private lateinit var containerLayout: LinearLayout
    private lateinit var totalConsumptionText: TextView
    private lateinit var totalCostText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meu_consumo)

        initViews()
        loadConsumptionReport()

        findViewById<ImageView>(R.id.btnVol).setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        containerLayout = findViewById(R.id.containerLayout)
        totalConsumptionText = findViewById(R.id.totalConsumptionText)
        totalCostText = findViewById(R.id.totalCostValue)
    }

    private fun loadConsumptionReport() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getConsumptionReport()
                }

                if (response.isSuccessful) {
                    response.body()?.let { report ->
                        displayReport(report)
                    }
                } else {
                    Toast.makeText(this@MeuConsumoActivity,
                        "Erro ao carregar relatório", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MeuConsumoActivity,
                    "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayReport(report: ConsumptionReport) {
        totalConsumptionText.text = "${report.totalConsumption} kWh/mês"
        totalCostText.text = "R$ ${String.format("%.2f", report.totalCost)}"

        report.reportUserApplianceList.forEach { applianceReport ->
            addApplianceCard(applianceReport)
        }
    }

    private fun addApplianceCard(applianceReport: ApplianceReport) {
        val cardView = layoutInflater.inflate(R.layout.appliance_card, containerLayout, false) as CardView

        // Set appliance icon based on ID
        val iconImageView = cardView.findViewById<ImageView>(R.id.applianceIcon)
        val iconResource = when (applianceReport.applianceId) {
            1 -> R.drawable.geladeira_icon
            2 -> R.drawable.luz_icon
            3 -> R.drawable.tv_icon
            4 -> R.drawable.chuverio_icon
            5 -> R.drawable.microondas_icon
            6 -> R.drawable.maquina_icon
            else -> R.drawable.geladeira_icon // default icon
        }
        iconImageView.setImageResource(iconResource)

        // Set card data
        cardView.findViewById<TextView>(R.id.quantityText).text =
            "Quantidade\n${applianceReport.quantity}"
        cardView.findViewById<TextView>(R.id.consumptionText).text =
            "Consumo Total\n${applianceReport.totalConsumption} kWh"
        cardView.findViewById<TextView>(R.id.costText).text =
            "Valor\nR$ ${String.format("%.2f", applianceReport.totalCost)}"

        containerLayout.addView(cardView)
    }
}