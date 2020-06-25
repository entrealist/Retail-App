package com.drugstore.data.webservice.customer.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class OrderModel(
    @Json(name = "id") val id: Int,
    @Json(name = "hash_id") val hashId: String,
    @Json(name = "status") val status: String,
    @Json(name = "status_title") val statusTitle: String,
    @Json(name = "price") val price: Double,
    @Json(name = "image") val image: String?,
    @Json(name = "products") val products: Array<OrderProductModel>,
    @Json(name = "shipping_tracking_id") val shippingTrackingId: String?,
    @Json(name = "sent_at") val sentAt: Long?,
    @Json(name = "created_at") val createdAt: Long,
    @Json(name = "prescription_link") val prescriptionLink: String,
    @Json(name = "order_link") val orderLink: String
)