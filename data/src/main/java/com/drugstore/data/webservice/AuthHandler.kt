package com.drugstore.data.webservice

import com.drugstore.data.webservice.auth.PutLoginService
import com.drugstore.data.webservice.auth.request.PutLoginRequest
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private val TOKEN_REFRESH_THRESHOLD = TimeUnit.MINUTES.toSeconds(10)

@Singleton
class AuthHandler @Inject constructor(
    val sessionHolder: SessionHolder,
    val putLoginService: PutLoginService
) {
    val mutex = Mutex()

    suspend inline operator fun <R> invoke(crossinline block: suspend () -> R) = withContext(IO) {
        if (isTokenRefreshNeeded()) {
            mutex.withLock {
                if (isTokenRefreshNeeded()) {
                    val request =
                        PutLoginRequest(
                            sessionHolder.token
                                ?: throw IllegalStateException("Unable to refresh session since there's no token"),
                            sessionHolder.refreshToken
                                ?: throw IllegalStateException("Unable to refresh session since there's no refresh token")
                        )
                    val response = putLoginService.putLogin(request)
                    sessionHolder.sessionUpdated(response.refreshToken, response.expireAt)
                }
            }
        }
        block()
    }

    fun isTokenRefreshNeeded(): Boolean {
        val currentTimestamp = Instant.now().epochSecond
        val tokenExpirationTimestamp = sessionHolder.tokenExpirationTimestamp
        return currentTimestamp + TOKEN_REFRESH_THRESHOLD >= tokenExpirationTimestamp
    }
}