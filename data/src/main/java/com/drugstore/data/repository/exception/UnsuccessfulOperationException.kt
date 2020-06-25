package com.drugstore.data.repository.exception

class UnsuccessfulOperationException(
    val code: Int,
    override val message: String
) : Exception(message)