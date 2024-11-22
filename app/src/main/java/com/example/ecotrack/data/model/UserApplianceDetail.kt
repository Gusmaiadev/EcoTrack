data class UserApplianceDetail(
    val id: Long,
    val user_id: Long,
    val appliance_id: Int,
    val associationDate: String,
    val minutesUsedPerDay: Double,
    val daysUsedPerWeek: Int,
    val totalConsumption: Double,
    val totalCost: Double
)