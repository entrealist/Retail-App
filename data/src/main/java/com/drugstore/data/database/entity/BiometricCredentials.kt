package com.drugstore.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "BiometricCredentials"
)
class BiometricCredentials(
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "encoded_password") var encodedPassword: String
) {

    @PrimaryKey
    var lock: Int = 1
        set(@Suppress("UNUSED_PARAMETER") value) { field = 1 }
}