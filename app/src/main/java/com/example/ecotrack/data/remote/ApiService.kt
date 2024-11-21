package com.example.ecotrack.data.remote

import Appliance
import LoginResponse
import UserApplianceRequest
import com.example.ecotrack.data.model.LoginRequest
import com.example.ecotrack.data.model.SignupRequest
import com.example.ecotrack.data.model.State
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("auth/signup")
    suspend fun signup(@Body signupRequest: SignupRequest): Response<Unit>

    @GET("state")
    suspend fun getStates(): Response<List<State>>

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("appliance")
    suspend fun getAppliances(): Response<List<Appliance>>

    @POST("userAppliance")
    suspend fun registerUserAppliance(@Body request: UserApplianceRequest): Response<Unit>

}