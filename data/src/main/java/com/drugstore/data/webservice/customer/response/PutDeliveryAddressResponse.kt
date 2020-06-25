package com.drugstore.data.webservice.customer.response

import com.drugstore.data.webservice.customer.model.AddressModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PutDeliveryAddressResponse(
    @Json(name = "message") val message: String,
    @Json(name = "address") val address: AddressModel
)