package com.example.ecotrack.data.model

data class SignupRequest(
    val name: String,
    val birthDate: String,
    val abbreviationState: String,
    val email: String,
    val password: String
)