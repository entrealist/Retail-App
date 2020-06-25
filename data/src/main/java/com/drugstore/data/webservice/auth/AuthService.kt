package com.drugstore.data.webservice.auth

import com.drugstore.data.webservice.AUTH_HEADER_NAME
import com.drugstore.data.webservice.NO_AUTH_HEADER
import com.drugstore.data.webservice.auth.request.PostLoginRequest
import com.drugstore.data.webservice.auth.request.RegistrationRequest
import com.drugstore.data.webservice.auth.response.DeleteLoginResponse
import com.drugstore.data.webservice.auth.response.PostFrontResponse
import com.drugstore.data.webservice.auth.response.PostLoginResponse
import com.drugstore.data.webservice.auth.response.RegistrationResponse
import retrofit2.http.*

interface AuthService {

    @POST("auth/registration")
    @Headers(NO_AUTH_HEADER)
    suspend fun registration(@Body request: RegistrationRequest): RegistrationResponse

    @POST("auth/login")
    @Headers(NO_AUTH_HEADER)
    suspend fun postLogin(@Body request: PostLoginRequest): PostLoginResponse

    @DELETE("auth/login")
    @Headers(NO_AUTH_HEADER)
    suspend fun deleteLogin(@Header(AUTH_HEADER_NAME) token: String): DeleteLoginResponse

    @POST("auth/front")
    suspend fun postFront(): PostFrontResponse
}