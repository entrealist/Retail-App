package com.drugstore.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity(
    tableName = "CacheRecords"
)
class CacheRecord(
    @PrimaryKey val key: String,
    @ColumnInfo(name = "expiration_timestamp") val expirationDateTime: LocalDateTime
)