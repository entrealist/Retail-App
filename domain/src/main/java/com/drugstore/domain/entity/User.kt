package com.drugstore.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate

@Entity(
    tableName = "User"
)
class User(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "uuid") val uuid: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "gender") val gender: Gender,
    @ColumnInfo(name = "birthday_date_timestamp") val birthdayDate: LocalDate,
    @ColumnInfo(name = "locale_id") val localeId: String
) {

    @PrimaryKey
    var lock: Int = 1
        set(@Suppress("UNUSED_PARAMETER") value) { field = 1 }
}