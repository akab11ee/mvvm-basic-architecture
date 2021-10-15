package example.com.myapplication.base

import androidx.annotation.CallSuper
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import example.com.myapplication.MyApplication
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.net.UnknownHostException

abstract class BaseObserver {
    fun resolveError(e: Throwable) {
        when (e) {
            is HttpException -> resolveHttpException(e)

            is JsonSyntaxException -> onFailure(RequestException.parsingException(e))

            is UnknownHostException -> onFailure(RequestException.networkError(e))

            else -> onFailure(RequestException.unexpectedError(e))
        }

    }

    private fun resolveHttpException(e: HttpException) {
        var errorResponse: JsonObject? = null

        try {
            errorResponse = Gson().fromJson<JsonObject>(
                e.response().errorBody()?.string(),
                JsonObject::class.java
            )
        } catch (js: Exception) {
            //Crashlytics.log("**body**${e.response().errorBody()?.string()}**error**${js.message}")
        }

        val errorMessage =
            errorResponse?.getAsJsonArray("errors")?.get(0)?.asJsonObject?.get("message")?.asString
                ?: e.message()

        val httpErrorCode =
            errorResponse?.getAsJsonArray("errors")?.get(0)?.asJsonObject?.get("httpCode")?.asString
                ?: ""

        val bodyErrorCode =
            errorResponse?.getAsJsonArray("errors")?.get(0)?.asJsonObject?.get("code")?.asString
                ?: ""

        when (e.code()) {
            401 -> {
                MyApplication.setApplicationLevelObject(
                    ApplicationLevelAction.ApplicationLevelActionObject(
                        true,
                        bodyErrorCode
                    )
                )
                onFailure(
                    RequestException.authenticationError(
                        e.code(),
                        errorMessage,
                        bodyErrorCode,
                        e
                    )
                )
            }

            400, 404, 500, 501, 502, 503, 504 ->
                onFailure(
                    RequestException.serviceError(
                        e.code(),
                        errorMessage,
                        exception = e,
                        httpStringCode = httpErrorCode,
                        bodyCode = bodyErrorCode
                    )
                )

            else ->
                onFailure(RequestException.httpException(e))
        }
    }

    open fun onFailure(requestException: RequestException) {}
    open fun showProgress(isShown: Boolean) {}
}

abstract class RequestObserver<T> : SingleObserver<T>, BaseObserver() {

    lateinit var disposable: Disposable

    @CallSuper
    override fun onSuccess(t: T) {
        showProgress(false)
    }

    @CallSuper
    override fun onSubscribe(d: Disposable) {
        disposable = d
        showProgress(true)
    }

    @CallSuper
    override fun onError(e: Throwable) {
        showProgress(false)
        resolveError(e)
    }
}

abstract class CompletableRequestObserver : CompletableObserver, BaseObserver() {
    lateinit var disposable: Disposable

    @CallSuper
    override fun onComplete() {
        showProgress(false)
    }

    @CallSuper
    override fun onSubscribe(d: Disposable) {
        disposable = d
        showProgress(true)
    }

    @CallSuper
    override fun onError(e: Throwable) {
        showProgress(false)
        resolveError(e)
    }
}