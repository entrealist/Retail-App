package com.drugstore.data.repository

import com.drugstore.data.webservice.auth.AuthService
import com.drugstore.data.webservice.auth.request.PostLoginRequest
import com.drugstore.data.webservice.auth.request.RegistrationRequest
import com.drugstore.data.webservice.customer.CustomerService
import com.drugstore.data.webservice.front.FrontService
import com.drugstore.data.webservice.front.request.PostPasswordRequest
import com.drugstore.domain.entity.Gender
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val customerService: CustomerService,
    private val frontService: FrontService,
    private val sessionRepository: SessionRepository
) {

    suspend fun register(
        email: String?, firstName: String?, lastName: String?, password: String?,
        repeatedPassword: String?, gender: Gender?, birthday: LocalDate?
    ) = withContext(Default) {
        val request =
            RegistrationRequest(
                email,
                firstName,
                lastName,
                password,
                repeatedPassword,
                gender?.reverseMap(),
                birthday?.reverseMap()
            )
        val response =
            call { authService.registration(request) }
        sessionRepository.sessionCreated(
            response.login.token,
            response.login.refreshToken,
            response.login.expireAt,
            response.customer.map()
        )
    }

    suspend fun login(email: String?, password: String?) = withContext(Default) {
        val loginRequest =
            PostLoginRequest(
                email,
                password
            )
        val loginResponse = call {
            authService.postLogin(loginRequest)
        }
        val getProfileResponse = call {
            customerService.getProfile(
                loginResponse.userId,
                loginResponse.token
            )
        }
        sessionRepository.sessionCreated(
            loginResponse.token,
            loginResponse.refreshToken,
            loginResponse.expireAt,
            getProfileResponse.customer.map()
        )
    }

    suspend fun logout() = withContext(Default) {
        sessionRepository.getToken()?.let {
            launch(NonCancellable) {
                authService.runCatching { deleteLogin(it) }
            }
        }
        sessionRepository.sessionRemoved()
    }

    suspend fun recoverPassword(email: String?) = withContext(Default) {
        val request =
            PostPasswordRequest(email)
        val response = call {
            frontService.postPassword(request)
        }
        return@withContext response.message
    }
}