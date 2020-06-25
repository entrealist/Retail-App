package com.drugstore.data.webservice.front.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CatalogItemModel(
    @Json(name = "id") val id: Int,
    @Json(name = "product_id") val productId: Int,
    @Json(name = "product_title") val productTitle: String,
    @Json(name = "qty") val qty: Int,
    @Json(name = "custom_qty_type_str") val customQtyTypeStr: String,
    @Json(name = "price_str") val priceStr: String
)