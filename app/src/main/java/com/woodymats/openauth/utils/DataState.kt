package com.woodymats.openauth.utils

import java.lang.Exception

data class Resource<out T>(val status: ApiCallStatus, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Resource<T> = Resource(status = ApiCallStatus.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): Resource<T> =
            Resource(status = ApiCallStatus.SERVERERROR, data = data, message = message)

        fun <T> loading(data: T?): Resource<T> = Resource(status = ApiCallStatus.LOADING, data = data, message = null)
    }
}
