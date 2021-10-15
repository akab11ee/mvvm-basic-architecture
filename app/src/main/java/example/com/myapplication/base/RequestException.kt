package example.com.myapplication.base

import com.google.gson.JsonSyntaxException
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import java.io.IOException

class RequestException(
    val intCode: Int = 0,
    override val message: String = "",
    val code: String = "",
    val kind: Kind = Kind.NONE,
    exception: Throwable,
    val bodyErrorCode: String = ""
) : RuntimeException(message, exception) {

    /**
     * Identifies the event kind which triggered a [RequestException].
     */
    enum class Kind {
        AUTHENTICATION_ERROR,
        SERVICE_ERROR,
        NOT_FOUND_ERROR,
        NO_NETWORK_ERROR,
        TIMEOUT_ERROR,
        UNEXPECTED,
        JSON_PARSING,
        NONE
    }

    companion object {

        internal fun serviceError(
            httpCode: Int = -1,
            message: String,
            httpStringCode: String,
            exception: HttpException,
            bodyCode: String = ""
        ): RequestException {
            return RequestException(
                httpCode,
                message,
                httpStringCode,
                Kind.SERVICE_ERROR,
                exception,
                bodyCode
            )
        }

        internal fun authenticationError(
            responseCode: Int = 0,
            message: String,
            code: String,
            exception: HttpException
        ): RequestException {
            return RequestException(
                responseCode,
                message,
                code,
                Kind.AUTHENTICATION_ERROR,
                exception
            )
        }

        internal fun notFountError(exception: HttpException): RequestException {
            return RequestException(
                message = getErrorString(Kind.NOT_FOUND_ERROR),
                kind = Kind.NOT_FOUND_ERROR,
                exception = exception
            )
        }

        internal fun httpException(exception: HttpException): RequestException {
            return RequestException(
                message = getErrorString(Kind.UNEXPECTED),
                kind = Kind.UNEXPECTED,
                exception = exception
            )
        }

        internal fun networkError(exception: IOException): RequestException {
            return RequestException(
                message = getErrorString(Kind.NO_NETWORK_ERROR),
                kind = Kind.NO_NETWORK_ERROR,
                exception = exception
            )
        }

        internal fun timeoutError(exception: IOException): RequestException {
            return RequestException(
                message = getErrorString(Kind.TIMEOUT_ERROR),
                kind = Kind.TIMEOUT_ERROR,
                exception = exception
            )
        }

        internal fun unexpectedError(exception: Throwable): RequestException {
            return RequestException(
                message = getErrorString(Kind.UNEXPECTED),
                kind = Kind.UNEXPECTED,
                exception = exception
            )
        }

        fun parsingException(je: JsonSyntaxException): RequestException {
            return RequestException(
                -1,
                message = getErrorString(Kind.JSON_PARSING),
                kind = Kind.JSON_PARSING,
                exception = je
            )
        }

        private fun getErrorString(kind: Kind) =
            when (kind) {
                else -> "Something went wrong"
            }
    }
}