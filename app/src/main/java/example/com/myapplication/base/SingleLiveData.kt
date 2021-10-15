package example.com.myapplication.base

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import example.com.myapplication.data.ApiResponse
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveData<T> : MutableLiveData<T>() {

    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        if (hasActiveObservers()) {
            Timber.i("has active observers")
        }

        // Observe the internal MutableLiveData
        super.observe(owner, Observer { t ->

            // compare and change status
            if (t is ApiResponse<*>) when (t) {
                is ApiResponse.Success<*> -> {
                    if (mPending.compareAndSet(true, false)) {
                        observer.onChanged(t)
                    }
                }

                is ApiResponse.Failure -> {
                    if (mPending.compareAndSet(true, false)) {
                        observer.onChanged(t)
                    }
                }

                is ApiResponse.Progress -> {
                    if (mPending.get())
                        observer.onChanged(t)
                }
            } else if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    @MainThread
    override fun postValue(t: T?) {
        mPending.set(true)
        super.postValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }
}