package com.drugstore.data.repository

import com.drugstore.data.repository.entity.Input.*
import com.drugstore.data.repository.exception.InvalidOperationOutputException
import com.drugstore.data.repository.exception.OperationException
import com.drugstore.data.repository.exception.OperationValidationException
import com.drugstore.data.repository.exception.UnsuccessfulOperationException
import com.drugstore.data.webservice.exception.ApiErrorException
import com.drugstore.data.webservice.exception.ApiValidationErrorException
import com.drugstore.data.webservice.exception.HttpException
import com.drugstore.data.webservice.exception.JsonDataException

internal inline fun <R> call(block: () -> R): R {
    try {
        return block()
    } catch (e: JsonDataException) {
        throw e.map()
    } catch (e: ApiValidationErrorException) {
        throw OperationValidationException(
            e.message,
            e.mapErrors()
        )
    } catch (e: ApiErrorException) {
        throw OperationException(
            e.code,
            e.message
        )
    } catch (e: HttpException) {
        throw UnsuccessfulOperationException(
            e.code,
            e.message
        )
    }
}

internal fun JsonDataException.map() =
    InvalidOperationOutputException()

internal fun ApiValidationErrorException.mapErrors() = errors
    ?.mapNotNull {
        when (it.key) {
            "email" -> EMAIL
            "first_name" -> FIRST_NAME
            "last_name" -> LAST_NAME
            "old_password" -> CURRENT_PASSWORD
            "password" -> PASSWORD
            "birthday" -> BIRTHDAY
            "name" -> NAME
            "content" -> CONTENT
            "address_type" -> ADDRESS_TYPE
            "title" -> TITLE
            "country_id" -> COUNTRY_ID
            "zip" -> POSTAL_CODE
            "city" -> CITY
            "street" -> STREET
            "house_number" -> HOUSE_NUMBER
            "company" -> COMPANY_NAME
            "packstation_address" -> PACKSTATION_ADDRESS
            "packstation_number" -> PACKSTATION_NUMBER
            "phone" -> PHONE_NUMBER
            else -> null
        }?.run {
            this to it.value[0]
        }
    }
    ?.toMap()