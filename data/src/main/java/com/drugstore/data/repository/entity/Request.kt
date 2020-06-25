package com.drugstore.data.repository.entity

import java.io.Serializable

data class Request(
    val url: String,
    val additionalHttpHeaders: Map<String, String>
) : Serializable