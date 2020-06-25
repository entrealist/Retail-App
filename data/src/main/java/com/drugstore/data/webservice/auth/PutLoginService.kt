package com.drugstore.data.webservice.auth

import com.drugstore.data.webservice.NO_AUTH_HEADER
import com.drugstore.data.webservice.auth.request.PutLoginRequest
import com.drugstore.data.webservice.auth.response.PutLoginResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.PUT

interface PutLoginService {

    @PUT("auth/login")
    @Headers(NO_AUTH_HEADER)
    suspend fun putLogin(@Body request: PutLoginRequest): PutLoginResponse
}