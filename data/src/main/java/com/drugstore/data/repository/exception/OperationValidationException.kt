package com.drugstore.data.repository.exception

import com.drugstore.data.repository.entity.Input

class OperationValidationException(
    override val message: String,
    val errors: Map<Input, String>?
) : Exception(message)