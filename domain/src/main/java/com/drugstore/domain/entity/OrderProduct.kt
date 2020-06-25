package com.drugstore.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "OrderProducts",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["order_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OrderProduct(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "order_id", index = true) val orderId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "amount_string") val amountString: String,
    @ColumnInfo(name = "quantity_string") val quantityString: String
)