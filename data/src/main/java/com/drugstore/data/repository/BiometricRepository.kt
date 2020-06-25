package com.drugstore.data.repository

import com.drugstore.data.database.dao.BiometricCredentialsDao
import com.drugstore.data.repository.core.CryptoUtils
import com.drugstore.data.webservice.auth.AuthService
import com.drugstore.data.webservice.auth.request.PostLoginRequest
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.crypto.Cipher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricRepository @Inject constructor(
    private val biometricCredentialsDao: BiometricCredentialsDao,
    private val authService: AuthService,
    private val userRepository: UserRepository
) {
    val isBiometricAuthenticationEnabled get() = userRepository.email.isEqualToBiometricEmail()

    fun isBiometricAuthenticationAvailableFor(email: Flow<String?>) =
        email.isEqualToBiometricEmail()

    private fun Flow<String?>.isEqualToBiometricEmail() =
        combine(biometricCredentialsDao.getEmail()) { string, biometricEmail ->
            string?.equals(biometricEmail) ?: false
        }

    suspend fun getCipher() = withContext(Default) { CryptoUtils.cipher }

    suspend fun disableBiometricAuthentication() = biometricCredentialsDao.delete()

    suspend fun enableBiometricAuthentication(password: String, cipher: Cipher) = withContext(Default) {
        val email = userRepository.getEmail() ?: return@withContext
        val encodedPassword = CryptoUtils.encode(password, cipher) ?: return@withContext
        biometricCredentialsDao.deleteAndInsert(email, encodedPassword)
    }

    suspend fun getPassword(cipher: Cipher) = withContext(Default) {
        biometricCredentialsDao.getEncodedPasswordSuspend()?.let { CryptoUtils.decode(it, cipher) }
    }

    suspend fun checkPassword(password: String?) = withContext(Default) {
        val email = userRepository.getEmail()
        val request =
            PostLoginRequest(
                email,
                password
            )
        call { authService.postLogin(request) }
        return@withContext
    }
}