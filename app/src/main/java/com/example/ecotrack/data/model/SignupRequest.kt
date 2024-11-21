package com.example.ecotrack.data.model

import com.google.gson.annotations.SerializedName

data class SignupRequest(
    val name: String,
    val birthDate: String,
    @SerializedName("abbreviationState")
    val abbreviationState: String,
    val email: String,
    val password: String
)