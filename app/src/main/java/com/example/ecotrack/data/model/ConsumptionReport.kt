data class ConsumptionReport(
    val totalConsumption: Double,
    val totalCost: Double,
    val reportUserApplianceList: List<ApplianceReport>
)