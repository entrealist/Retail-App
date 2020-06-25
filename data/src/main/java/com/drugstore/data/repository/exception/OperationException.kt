package com.drugstore.data.repository.exception

class OperationException(
    val code: Int?,
    override val message: String
) : Exception(message)