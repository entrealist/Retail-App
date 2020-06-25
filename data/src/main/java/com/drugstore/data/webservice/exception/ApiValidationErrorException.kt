package com.drugstore.data.webservice.exception

class ApiValidationErrorException(
    override val message: String,
    val errors: Map<String, Array<String>>?
) : Exception(message)