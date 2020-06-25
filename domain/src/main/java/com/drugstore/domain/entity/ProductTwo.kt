package com.drugstore.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProductTwos",
    foreignKeys = [
        ForeignKey(
            entity = ProductOne::class,
            parentColumns = ["id"],
            childColumns = ["product_one_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProductTwo(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "product_one_id", index = true) val productOneId: Int,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "quantity_string") val quantityString: String,
    @ColumnInfo(name = "price_string") val priceString: String
)