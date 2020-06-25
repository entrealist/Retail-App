package com.drugstore.data.repository

import com.drugstore.data.database.dao.*
import com.drugstore.data.webservice.SessionHolder
import com.drugstore.domain.entity.User
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val cacheRecordsRepository: CacheRecordsRepository,
    private val addressesDao: AddressesDao,
    private val categoriesDao: CategoriesDao,
    private val contactDetailsDao: ContactDetailsDao,
    private val generalInfoDao: GeneralInfoDao,
    private val ordersDao: OrdersDao,
    private val sessionDao: SessionDao,
    private val userRepository: UserRepository
): SessionHolder {
    val regionId get() = sessionDao.getRegionId()
    internal suspend fun getToken() = sessionDao.getTokenSuspend()
    val isUserAuthenticated = sessionDao.getToken().map { it != null }
    val isUserAuthenticatedBlocking get() = sessionDao.getTokenBlocking() != null

    internal suspend fun sessionCreated(token: String, refreshToken: String, tokenExpirationTimestamp: Long, user: User) {
        sessionDao.upsertBatch(token, refreshToken, tokenExpirationTimestamp)
        userRepository.setUser(user)
        cacheRecordsRepository.setNonPersistentCacheExpiration(
            CustomerRepository.KEY_PROFILE,
            CustomerRepository.LIFESPAN_PROFILE
        )
    }

    internal suspend fun sessionUpdated(user: User) {
        userRepository.setUser(user)
        cacheRecordsRepository.setNonPersistentCacheExpiration(
            CustomerRepository.KEY_PROFILE,
            CustomerRepository.LIFESPAN_PROFILE
        )
    }

    internal suspend fun sessionRemoved() {
        sessionDao.upsertBatch(null, null, 0)
        cacheRecordsRepository.expireAllNonPersistentCaches()
        cacheRecordsRepository.expireAllCachesExcept(
            RegionsRepository.KEY_REGIONS,
            CommonRepository.KEY_LEGAL_INFO,
            CommonRepository.KEY_LINKS_INFO
        )
    }

    suspend fun setRegion(regionId: String) {
        sessionDao.upsertRegionId(regionId)
        addressesDao.delete()
        categoriesDao.delete()
        contactDetailsDao.delete()
        generalInfoDao.delete()
        ordersDao.delete()
        cacheRecordsRepository.expireAllCachesExcept(
            RegionsRepository.KEY_REGIONS
        )
    }

    fun onApplicationStopped() {
        cacheRecordsRepository.expireNonPersistentCache(CustomerRepository.KEY_PROFILE)
    }

    override val localeId get() = sessionDao.getRegionIdBlocking()
    override val token get() = sessionDao.getTokenBlocking()
    override val refreshToken get() = sessionDao.getRefreshTokenBlocking()
    override val tokenExpirationTimestamp get() = sessionDao.getTokenExpirationTimestampBlocking()

    override fun sessionUpdated(refreshToken: String, expireAt: Long) = runBlocking {
        sessionDao.upsertBatch(refreshToken, expireAt)
    }

    override fun sessionInvalidated() = runBlocking {
        sessionRemoved()
    }
}