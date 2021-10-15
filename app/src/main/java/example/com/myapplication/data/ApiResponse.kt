package example.com.myapplication.data

import androidx.lifecycle.LiveData
import example.com.myapplication.base.RequestException

sealed class ApiResponse<out T : Any> {
    data class Success<T : Any>(val items: T?) : ApiResponse<T>()
    data class Failure(val error: RequestException) : ApiResponse<Nothing>()
    data class Progress(val progress: Boolean) : ApiResponse<Nothing>()
}

fun <T : Any> LiveData<ApiResponse<T>>.isDataLoaded(): Boolean {
    when (this.value) {
        is ApiResponse.Success -> return true
    }
    return false
}

/**
 * true if [ApiResponse] is of type [Success] and [items] holds non-null
 */
val ApiResponse<*>.succeeded: Boolean
    get() = this is ApiResponse.Success && items != null