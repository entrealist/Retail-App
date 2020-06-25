package com.drugstore.data.database.dao

import androidx.room.*
import com.drugstore.data.database.entity.Session
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SessionDao {

    @Query("SELECT region_id FROM Session")
    abstract fun getRegionId(): Flow<String?>

    @Query("SELECT region_id FROM Session")
    abstract fun getRegionIdBlocking(): String?

    @Query("SELECT token FROM Session")
    abstract fun getToken(): Flow<String?>

    @Query("SELECT token FROM Session")
    abstract suspend fun getTokenSuspend(): String?

    @Query("SELECT token FROM Session")
    abstract fun getTokenBlocking(): String?

    @Query("SELECT refresh_token FROM Session")
    abstract fun getRefreshTokenBlocking(): String?

    @Query("SELECT token_expiration_timestamp FROM Session")
    abstract fun getTokenExpirationTimestampBlocking(): Long

    @Transaction
    open suspend fun upsertRegionId(regionId: String?) {
        val id = insert(
            Session(
                regionId = regionId
            )
        )
        if (id == -1L) updateRegionId(regionId)
    }

    @Transaction
    open suspend fun upsertBatch(token: String?, refreshToken: String?, tokenExpirationTimestamp: Long) {
        val id = insert(
            Session(
                token = token,
                refreshToken = refreshToken,
                tokenExpirationTimestamp = tokenExpirationTimestamp
            )
        )
        if (id == -1L) updateBatch(token, refreshToken, tokenExpirationTimestamp)
    }

    @Transaction
    open suspend fun upsertBatch(refreshToken: String?, tokenExpirationTimestamp: Long) {
        val id = insert(
            Session(
                refreshToken = refreshToken,
                tokenExpirationTimestamp = tokenExpirationTimestamp
            )
        )
        if (id == -1L) updateBatch(refreshToken, tokenExpirationTimestamp)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insert(session: Session): Long

    @Query("UPDATE Session SET region_id = :regionId")
    protected abstract suspend fun updateRegionId(regionId: String?)

    @Query("UPDATE Session SET token = :token, refresh_token = :refreshToken, token_expiration_timestamp = :tokenExpirationTimestamp")
    protected abstract suspend fun updateBatch(token: String?, refreshToken: String?, tokenExpirationTimestamp: Long)

    @Query("UPDATE Session SET refresh_token = :refreshToken, token_expiration_timestamp = :tokenExpirationTimestamp")
    protected abstract suspend fun updateBatch(refreshToken: String?, tokenExpirationTimestamp: Long)
}