package com.drugstore.data.repository

import com.drugstore.data.webservice.customer.CustomerService
import com.drugstore.data.webservice.customer.request.PutPasswordRequest
import com.drugstore.data.webservice.customer.request.PutProfileRequest
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.threeten.bp.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val cacheRecordsRepository: CacheRecordsRepository,
    private val customerService: CustomerService,
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) {
    private val mutex = Mutex()

    suspend fun updateProfile() = withContext(Default) {
        if (cacheRecordsRepository.isNonPersistentCacheFresh(KEY_PROFILE)) return@withContext
        mutex.withLock {
            if (cacheRecordsRepository.isNonPersistentCacheFresh(KEY_PROFILE)) return@withLock

            val userId = userRepository.getId()
            val response = call {
                customerService.getProfile(userId)
            }
            sessionRepository.sessionUpdated(response.customer.map())
        }
    }

    suspend fun updateProfile(email: String?) = withContext(Default) {
        val userId = userRepository.getId()
        val request =
            PutProfileRequest(
                email
            )
        val response = call {
            customerService.putProfile(
                userId,
                request
            )
        }
        cacheRecordsRepository.setNonPersistentCacheExpiration(KEY_PROFILE, Duration.ofMinutes(1))
        return@withContext response.message
    }

    suspend fun changePassword(
        currentPassword: String?, newPassword: String?, repeatedNewPassword: String?
    ) = withContext(Default) {
        val userId = userRepository.getId()
        val request =
            PutPasswordRequest(
                currentPassword,
                newPassword,
                repeatedNewPassword
            )
        val response = call {
            customerService.putPassword(
                userId,
                request
            )
        }
        return@withContext response.message
    }

    companion object {
        const val KEY_PROFILE = "profile"
        val LIFESPAN_PROFILE: Duration = Duration.ofHours(2)
    }
}