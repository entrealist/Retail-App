package com.drugstore.data.webservice.customer.response

import com.drugstore.data.webservice.model.CustomerModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetProfileResponse (
   @Json(name = "customer") val customer: CustomerModel
)