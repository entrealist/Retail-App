package com.drugstore.data.webservice.exception

class HttpException(
    val code: Int,
    override val message: String
) : Exception(message)