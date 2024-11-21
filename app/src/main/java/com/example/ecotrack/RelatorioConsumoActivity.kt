package com.example.ecotrack

import MonthlyReport
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ecotrack.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RelatorioConsumoActivity : AppCompatActivity() {
    private lateinit var containerLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relatorio_consumo)

        initViews()
        loadMonthlyReports()

        findViewById<ImageView>(R.id.btnVol).setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        containerLayout = findViewById(R.id.containerLayout)
    }

    private fun loadMonthlyReports() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getMonthlyReports()
                }

                if (response.isSuccessful) {
                    response.body()?.let { reports ->
                        displayReports(reports)
                    }
                } else {
                    Toast.makeText(this@RelatorioConsumoActivity,
                        "Erro ao carregar relatórios", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RelatorioConsumoActivity,
                    "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayReports(reports: List<MonthlyReport>) {
        containerLayout.removeAllViews() // Limpa views existentes

        reports.forEach { report ->
            addReportCard(report)
        }
    }

    private fun addReportCard(report: MonthlyReport) {
        val cardView = layoutInflater.inflate(R.layout.monthly_report_card, containerLayout, false)

        // Formatar a data de "2024-11" para "11/2024"
        val dateParts = report.monthYear.split("-")
        val formattedDate = "${dateParts[1]}/${dateParts[0]}"

        // Calcular CO2 (aproximadamente 0.11 kg por kWh)
        val co2 = report.totalConsumption * 0.11

        cardView.findViewById<TextView>(R.id.dateText).text = formattedDate
        cardView.findViewById<TextView>(R.id.consumptionText).text =
            "${String.format("%.0f", report.totalConsumption)} kWh/mês"
        cardView.findViewById<TextView>(R.id.costText).text =
            "R$ ${String.format("%.2f", report.totalCost)}"
        cardView.findViewById<TextView>(R.id.co2Text).text =
            "${String.format("%.1f", co2)} kg"

        containerLayout.addView(cardView)
    }
}