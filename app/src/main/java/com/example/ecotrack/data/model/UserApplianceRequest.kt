data class UserApplianceRequest(
    val appliance_id: Long,
    val hoursUsedPerDay: Double,
    val daysUsedPerWeek: Int
)