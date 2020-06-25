package com.drugstore.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "ContactDetails"
)
data class ContactDetail(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "data") val data: String,
    @ColumnInfo(name = "type") val type: Type
) {

    enum class Type(val id: Int) {
        ADDRESS(1),
        EMAIL(2),
        PHONE(3),
        SCHEDULE(4);

        companion object {
            private val values = values()

            fun getById(id: Int) = values.find { it.id == id }
        }
    }
}