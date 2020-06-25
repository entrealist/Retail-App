package com.drugstore.data.webservice.customer

import com.drugstore.data.webservice.AUTH_HEADER_NAME
import com.drugstore.data.webservice.NO_AUTH_HEADER
import com.drugstore.data.webservice.customer.model.AddressModel
import com.drugstore.data.webservice.customer.model.OrderModel
import com.drugstore.data.webservice.customer.request.PutDeliveryAddressRequest
import com.drugstore.data.webservice.customer.request.PutPasswordRequest
import com.drugstore.data.webservice.customer.request.PutProfileRequest
import com.drugstore.data.webservice.customer.response.GetProfileResponse
import com.drugstore.data.webservice.customer.response.PutDeliveryAddressResponse
import com.drugstore.data.webservice.response.SuccessResponse
import retrofit2.http.*

interface CustomerService {

    @GET("customer/{customer_id}/order/index")
    suspend fun getOrderIndex(@Path("customer_id") customerId: Int): Array<OrderModel>

    @GET("customer/{customer_id}/delivery_address")
    suspend fun getDeliveryAddress(@Path("customer_id") customerId: Int): Array<AddressModel>

    @PUT("customer/{customer_id}/delivery_address")
    suspend fun putDeliveryAddress(@Path("customer_id") customerId: Int, @Body request: PutDeliveryAddressRequest): PutDeliveryAddressResponse

    @GET("customer/{customer_id}/profile")
    @Headers(NO_AUTH_HEADER)
    suspend fun getProfile(@Path("customer_id") customerId: Int, @Header(AUTH_HEADER_NAME) token: String): GetProfileResponse

    @GET("customer/{customer_id}/profile")
    suspend fun getProfile(@Path("customer_id") customerId: Int): GetProfileResponse

    @PUT("customer/{customer_id}/profile")
    suspend fun putProfile(@Path("customer_id") customerId: Int, @Body request: PutProfileRequest): SuccessResponse

    @PUT("customer/{customer_id}/password")
    suspend fun putPassword(@Path("customer_id") customerId: Int, @Body request: PutPasswordRequest): SuccessResponse
}