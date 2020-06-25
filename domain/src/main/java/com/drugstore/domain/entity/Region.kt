package com.drugstore.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Regions"
)
data class Region(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @Embedded val country: Country
) {

    data class Country(
        @ColumnInfo(name = "country_id") val id: String,
        @ColumnInfo(name = "country_name") val name: String
    )
}