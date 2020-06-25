package com.drugstore.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drugstore.data.database.entity.CacheRecord
import org.threeten.bp.LocalDateTime

@Dao
abstract class CacheRecordsDao {

    @Query("SELECT expiration_timestamp FROM CacheRecords WHERE `key` = :key")
    abstract suspend fun getExpirationDateTime(key: String): LocalDateTime?

    suspend fun insert(key: String, expirationDateTime: LocalDateTime) =
        insert(
            CacheRecord(
                key,
                expirationDateTime
            )
        )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insert(cacheRecord: CacheRecord)

    @Query("DELETE FROM CacheRecords WHERE `key` = :key")
    abstract suspend fun delete(key: String)

    @Query("DELETE FROM CacheRecords WHERE `key` LIKE :pattern")
    abstract suspend fun deleteWhereKeyLike(pattern: String)

    @Query("DELETE FROM CacheRecords WHERE `key` NOT IN (:keys)")
    abstract suspend fun deleteWhereKeyNotIn(vararg keys: String)
}