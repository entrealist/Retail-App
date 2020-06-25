package com.drugstore.data.webservice.customer.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class OrderProductModel(
    @Json(name = "title") val title: String,
    @Json(name = "price_str") val priceStr: String,
    @Json(name = "quantity") val quantity: Int,
    @Json(name = "qty_type") val qtyType: String
)