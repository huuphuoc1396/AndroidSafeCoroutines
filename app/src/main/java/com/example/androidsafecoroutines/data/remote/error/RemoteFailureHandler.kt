package com.example.androidsafecoroutines.data.remote.error

import com.example.androidsafecoroutines.data.remote.response.ServerErrorResponse
import com.example.androidsafecoroutines.ext.default
import com.example.androidsafecoroutines.ext.defaultEmpty
import com.example.androidsafecoroutines.safecoroutines.failure.FailureHandler
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RemoteFailureHandler @Inject constructor() : FailureHandler {
    override fun handleThrowable(throwable: Throwable) = when (throwable) {
        is IOException -> ApiError.Connection
        is HttpException -> handleHttpException(throwable)
        else -> null
    }

    private fun handleHttpException(httpException: HttpException): ApiError.Server {
        val code = httpException.code().default(-1)
        val errorBody = httpException.response()?.errorBody()?.string()
        val errorMessage = try {
            val serverErrorResponse = Gson().fromJson(errorBody, ServerErrorResponse::class.java)
            serverErrorResponse.message.defaultEmpty()
        } catch (parseException: Exception) {
            ""
        }
        return ApiError.Server(
            code = code,
            errorMessage = errorMessage
        )
    }
}