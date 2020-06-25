package com.drugstore.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProductOnes",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProductOne(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "product_id", index = true) val productId: Int,
    @ColumnInfo(name = "title") val title: String
)