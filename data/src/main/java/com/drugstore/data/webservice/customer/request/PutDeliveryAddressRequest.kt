package com.drugstore.data.webservice.customer.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PutDeliveryAddressRequest(
    @Json(name = "address_type") val addressType: String,
    @Json(name = "title") val title: String?,
    @Json(name = "first_name") val firstName: String?,
    @Json(name = "last_name") val lastName: String?,
    @Json(name = "country_id") val countryId: String?,
    @Json(name = "zip") val zip: String?,
    @Json(name = "city") val city: String?,
    @Json(name = "street") val street: String?,
    @Json(name = "house_number") val houseNumber: String?,
    @Json(name = "company") val company: String?,
    @Json(name = "packstation_address") val packstationAddress: String?,
    @Json(name = "packstation_number") val packstationNumber: String?,
    @Json(name = "phone") val phone: String?
)