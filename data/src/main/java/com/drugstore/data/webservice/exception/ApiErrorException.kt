package com.drugstore.data.webservice.exception

class ApiErrorException(
    val code: Int?,
    override val message: String
) : Exception(message)