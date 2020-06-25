package com.drugstore.data.webservice.auth

import com.drugstore.data.webservice.AuthHandler
import com.drugstore.data.webservice.ErrorHandler
import com.drugstore.data.webservice.auth.request.PostLoginRequest
import com.drugstore.data.webservice.auth.request.RegistrationRequest

internal class AuthServiceProxy(
    private val errorHandler: ErrorHandler,
    private val authHandler: AuthHandler,
    private val authService: AuthService
) : AuthService {

    override suspend fun registration(request: RegistrationRequest) =
        errorHandler { authService.registration(request) }

    override suspend fun postLogin(request: PostLoginRequest) =
        errorHandler { authService.postLogin(request) }

    override suspend fun deleteLogin(token: String) =
        errorHandler { authService.deleteLogin(token) }

    override suspend fun postFront() =
        errorHandler { authHandler.invoke { authService.postFront() } }
}