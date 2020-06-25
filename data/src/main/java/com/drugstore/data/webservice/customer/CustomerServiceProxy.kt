package com.drugstore.data.webservice.customer

import com.drugstore.data.webservice.AuthHandler
import com.drugstore.data.webservice.ErrorHandler
import com.drugstore.data.webservice.customer.request.PutDeliveryAddressRequest
import com.drugstore.data.webservice.customer.request.PutPasswordRequest
import com.drugstore.data.webservice.customer.request.PutProfileRequest

internal class CustomerServiceProxy(
    private val errorHandler: ErrorHandler,
    private val authHandler: AuthHandler,
    private val customerService: CustomerService
) : CustomerService {

    override suspend fun getOrderIndex(customerId: Int) =
        errorHandler { authHandler.invoke { customerService.getOrderIndex(customerId) } }

    override suspend fun getDeliveryAddress(customerId: Int) =
        errorHandler { authHandler.invoke { customerService.getDeliveryAddress(customerId) } }

    override suspend fun putDeliveryAddress(customerId: Int, request: PutDeliveryAddressRequest) =
        errorHandler { authHandler.invoke { customerService.putDeliveryAddress(customerId, request) } }

    override suspend fun getProfile(customerId: Int, token: String) =
        errorHandler { customerService.getProfile(customerId, token) }

    override suspend fun getProfile(customerId: Int) =
        errorHandler { authHandler.invoke { customerService.getProfile(customerId) } }

    override suspend fun putProfile(customerId: Int, request: PutProfileRequest) =
        errorHandler { authHandler.invoke { customerService.putProfile(customerId, request) } }

    override suspend fun putPassword(customerId: Int, request: PutPasswordRequest) =
        errorHandler { authHandler.invoke { customerService.putPassword(customerId, request) } }
}