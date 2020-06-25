package com.drugstore.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity(
    tableName = "Orders"
)
data class Order(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "hash_id") val hashId: String,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "status_id") val statusId: String,
    @ColumnInfo(name = "status_title") val statusTitle: String,
    @ColumnInfo(name = "total_amount_string") val totalAmountString: String,
    @ColumnInfo(name = "imageUri") val imageUri: String?,
    @ColumnInfo(name = "order_timestamp") val orderDateTime: LocalDateTime,
    @ColumnInfo(name = "shipping_timestamp") val shippingDateTime: LocalDateTime?,
    @ColumnInfo(name = "shipping_id") val shippingId: String?,
    @ColumnInfo(name = "prescription_url") val prescriptionUrl: String,
    @ColumnInfo(name = "order_url") val orderUrl: String
)