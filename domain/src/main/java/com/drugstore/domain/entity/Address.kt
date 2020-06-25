package com.drugstore.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Addresses"
)
data class Address(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "type") val type: Type,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @Embedded val country: Region.Country,
    @ColumnInfo(name = "postal_code") val postalCode: String,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "street") val street: String?, // REGULAR | required
    @ColumnInfo(name = "house_number") val houseNumber: String?, // REGULAR | required
    @ColumnInfo(name = "company_name") val companyName: String?, // REGULAR
    @ColumnInfo(name = "packstation_address") val packstationAddress: String?, // DHL_PACKSTATION | required
    @ColumnInfo(name = "packstation_number") val packstationNumber: String?, // DHL_PACKSTATION | required
    @ColumnInfo(name = "phone_number") val phoneNumber: String?
) {

    enum class Type(val id: Int) {
        REGULAR(1),
        DHL_PACKSTATION(2);

        companion object {
            val values = values()
            val ids = IntArray(values.size) { i -> values[i].id }

            fun getById(id: Int) = values.find { it.id == id }
        }
    }
}