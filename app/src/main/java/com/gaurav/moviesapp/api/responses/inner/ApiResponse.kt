package com.gaurav.moviesapp.api.responses.inner

import retrofit2.Response

sealed class ApiResponse<T> {
    companion object {
        fun <T> create(error: Throwable): ApiResponse<T> {
            return ApiErrorResponse(error.message ?: "Unknown Error\nCheck Network Connection")
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204){ //204 means empty Response
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(
                        body = body
                    )
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(errorMsg ?: "Unknown Error\n" +
                "Check Network Connection")
            }
        }
    }
}

class ApiEmptyResponse<T> : ApiResponse<T>()
data class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>()
data class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()