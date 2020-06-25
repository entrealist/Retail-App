package com.drugstore.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Session"
)
class Session(
    @ColumnInfo(name = "region_id") var regionId: String? = null,
    @ColumnInfo(name = "token") var token: String? = null,
    @ColumnInfo(name = "refresh_token") var refreshToken: String? = null,
    @ColumnInfo(name = "token_expiration_timestamp") var tokenExpirationTimestamp: Long = 0
) {

    @PrimaryKey
    var lock: Int = 1
        set(@Suppress("UNUSED_PARAMETER") value) { field = 1 }
}