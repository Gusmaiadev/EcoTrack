package com.example.ecotrack

import ApplianceReport
import ConsumptionReport
import UserApplianceDetail
import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
    private var applianceReports: List<ApplianceReport> = emptyList()

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
                        applianceReports = report.reportUserApplianceList
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
        containerLayout.removeAllViews()

        val groupedAppliances = report.reportUserApplianceList.groupBy { it.applianceId }

        groupedAppliances.forEach { (applianceId, appliances) ->
            val cardView = createApplianceCard(applianceId, appliances)
            cardView.setOnClickListener {
                loadUserApplianceDetails(applianceId)
            }
            containerLayout.addView(cardView)
        }
    }

    private fun createApplianceCard(applianceId: Int, appliances: List<ApplianceReport>): CardView {
        val cardView = layoutInflater.inflate(R.layout.appliance_card, containerLayout, false) as CardView

        val iconImageView = cardView.findViewById<ImageView>(R.id.applianceIcon)
        val iconResource = when (applianceId) {
            1 -> R.drawable.geladeira_icon
            2 -> R.drawable.luz_icon
            3 -> R.drawable.tv_icon
            4 -> R.drawable.chuverio_icon
            5 -> R.drawable.microondas_icon
            6 -> R.drawable.maquina_icon
            else -> R.drawable.geladeira_icon
        }
        iconImageView.setImageResource(iconResource)

        val totalConsumption = appliances.sumOf { it.totalConsumption }
        val totalCost = appliances.sumOf { it.totalCost }

        cardView.findViewById<TextView>(R.id.quantityText).text =
            "Quantidade\n${appliances.size}"
        cardView.findViewById<TextView>(R.id.consumptionText).text =
            "Consumo Total\n${totalConsumption} kWh"
        cardView.findViewById<TextView>(R.id.costText).text =
            "Valor\nR$ ${String.format("%.2f", totalCost)}"

        return cardView
    }

    private fun loadUserApplianceDetails(applianceId: Int) {
        lifecycleScope.launch {
            try {
                val dialog = createLoadingDialog()
                dialog.show()

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getUserAppliancesByType(applianceId)
                }

                dialog.dismiss()

                if (response.isSuccessful && response.body() != null) {
                    showApplianceOptionsDialog(response.body()!!)
                } else {
                    Toast.makeText(
                        this@MeuConsumoActivity,
                        "Erro ao carregar detalhes do eletrodoméstico",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MeuConsumoActivity,
                    "Erro de conexão: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun createLoadingDialog(): Dialog {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        return dialog
    }

    private fun showApplianceOptionsDialog(appliances: List<UserApplianceDetail>) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_appliance_options)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val optionsContainer = dialog.findViewById<LinearLayout>(R.id.optionsContainer)

        appliances.forEach { appliance ->
            val optionView = layoutInflater.inflate(R.layout.item_appliance_option, optionsContainer, false)

            // Formatando o texto para mostrar uso diário e semanal
            val usageInfo = buildString {
                append("Minutos por dia: ${appliance.minutesUsedPerDay.toInt()}\n")
                append("Dias por semana: ${appliance.daysUsedPerWeek}\n")
                append("Consumo: ${String.format("%.2f", appliance.totalConsumption)} kWh\n")
                append("Custo: R$ ${String.format("%.2f", appliance.totalCost)}")
            }

            optionView.findViewById<TextView>(R.id.optionTitle).text = "Opção ${appliances.indexOf(appliance) + 1}"
            optionView.findViewById<TextView>(R.id.optionDays).text = usageInfo

            optionView.findViewById<Button>(R.id.optionDelete).setOnClickListener {
                deleteAppliance(appliance.id)
                dialog.dismiss()
            }

            optionsContainer.addView(optionView)
        }

        dialog.show()
    }

    private fun deleteAppliance(userApplianceId: Long) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.deleteUserAppliance(userApplianceId)
                }

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@MeuConsumoActivity,
                        "Eletrodoméstico excluído com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadConsumptionReport() // Recarrega os dados
                } else {
                    Toast.makeText(
                        this@MeuConsumoActivity,
                        "Erro ao excluir eletrodoméstico",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MeuConsumoActivity,
                    "Erro de conexão: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}