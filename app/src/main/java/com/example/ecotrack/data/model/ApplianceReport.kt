data class ApplianceReport(
    val totalConsumption: Double,
    val totalCost: Double,
    val quantity: Int,
    val applianceId: Int,
    val userApplianceId: Long,
    val daysUsedPerWeek: Int,
    val minutesUsedPerDay: Int
)