package com.drugstore.data.webservice

import com.drugstore.data.webservice.exception.ApiErrorException
import com.drugstore.data.webservice.exception.ApiValidationErrorException
import com.drugstore.data.webservice.response.ErrorResponse
import com.drugstore.data.webservice.response.ValidationErrorResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor(
    moshi: Moshi
) {
    val errorResponseJsonAdapter: JsonAdapter<ErrorResponse> =
        moshi.adapter(ErrorResponse::class.java)
    val validationErrorResponseJsonAdapter: JsonAdapter<ValidationErrorResponse> =
        moshi.adapter(ValidationErrorResponse::class.java)

    suspend inline operator fun <R> invoke(crossinline block: suspend () -> R) = withContext(IO) {
        try {
            block()
        } catch (e: JsonDataException) {
            throw com.drugstore.data.webservice.exception.JsonDataException()
        } catch (e: HttpException) {
            try {
                val response = e.response() ?: throw e.map()
                val errorBodyString = response.errorBody()?.use { it.string() } ?: throw e.map()
                throw if (response.code() == 422) {
                    validationErrorResponseJsonAdapter.fromJson(errorBodyString)?.run {
                        ApiValidationErrorException(
                            message,
                            errors
                        )
                    } ?: e.map()
                } else {
                    errorResponseJsonAdapter.fromJson(errorBodyString)?.run {
                        ApiErrorException(
                            code,
                            message
                        )
                    } ?: e.map()
                }
            } catch (ignored: JsonDataException) {
                throw e.map()
            } catch (ignored: IOException) {
                throw e.map()
            }
        }
    }
}

fun HttpException.map() =
    com.drugstore.data.webservice.exception.HttpException(code(), message())