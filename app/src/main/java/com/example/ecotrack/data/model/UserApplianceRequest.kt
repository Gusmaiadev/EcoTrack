data class UserApplianceRequest(
    val appliance_id: Long,
    val minutesUsedPerDay: Int,
    val daysUsedPerWeek: Int
)