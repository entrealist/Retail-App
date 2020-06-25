package com.drugstore.data.repository

import com.drugstore.data.database.dao.CacheRecordsDao
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalDateTime.now
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheRecordsRepository @Inject constructor(
    private val cacheRecordsDao: CacheRecordsDao
) {
    private val cacheRecords = HashMap<String, LocalDateTime>()

    internal suspend fun setCacheExpiration(key: String, lifespan: Duration) {
        val expirationDateTime = now().plus(lifespan)
        cacheRecordsDao.insert(key, expirationDateTime)
    }

    internal suspend fun isCacheFresh(key: String): Boolean {
        val expirationDateTime = cacheRecordsDao.getExpirationDateTime(key)
        return if (expirationDateTime != null) {
            now().isBefore(expirationDateTime)
        } else {
            false
        }
    }

    internal suspend fun isCacheExpired(key: String) = !isCacheFresh(key)

    internal suspend fun expireAllCachesExcept(vararg keys: String) {
        cacheRecordsDao.deleteWhereKeyNotIn(*keys)
    }

    internal suspend fun expireCachesThatStartWith(keyPrefix: String) {
        cacheRecordsDao.deleteWhereKeyLike("$keyPrefix%")
    }

    internal fun setNonPersistentCacheExpiration(key: String, lifespan: Duration) {
        val expirationDateTime = now().plus(lifespan)
        cacheRecords[key] = expirationDateTime
    }

    internal fun isNonPersistentCacheFresh(key: String): Boolean {
        val expirationDateTime = cacheRecords[key]
        return if (expirationDateTime != null) {
            now().isBefore(expirationDateTime)
        } else {
            false
        }
    }

    internal fun expireAllNonPersistentCaches() {
        cacheRecords.clear()
    }

    internal fun expireNonPersistentCache(key: String) {
        cacheRecords.remove(key)
    }
}