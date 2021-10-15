package example.com.myapplication.base

import androidx.lifecycle.MutableLiveData
import example.com.myapplication.data.ApiResponse
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

interface CompositeDisposableContainer {
    fun getCompositeDisposable() : CompositeDisposable
}

class SingleWrapper<T:Any> constructor(val single : Single<T>) {
    fun subscribe(disposableContainer: CompositeDisposableContainer, observer: RequestObserver<in T>){
        single.subscribe(observer)
        disposableContainer.getCompositeDisposable().add(observer.disposable)
    }

    fun subscribe(disposableContainer: CompositeDisposableContainer, liveData: MutableLiveData<ApiResponse<T>>, observer: RequestObserver<in T>){
        subscribe(disposableContainer, object : RequestObserver<T>(){
            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                observer.onSubscribe(d)
            }

            override fun onSuccess(t: T) {
                super.onSuccess(t)
                observer.onSuccess(t)
                liveData.value = ApiResponse.Success(t)
            }

            override fun onFailure(requestException: RequestException) {
                super.onFailure(requestException)
                observer.onFailure(requestException)
                liveData.value = ApiResponse.Failure(requestException)
            }

            override fun showProgress(isShown: Boolean) {
                super.showProgress(isShown)
                observer.showProgress(isShown)
                liveData.value = ApiResponse.Progress(isShown)
            }
        })
    }

    fun subscribe(disposableContainer: CompositeDisposableContainer, liveData: MutableLiveData<ApiResponse<T>>? = null,
                  onSuccess: Consumer<in T>? = null, onFailure: Consumer<in Throwable>? = null){
        subscribe(disposableContainer, object : RequestObserver<T>(){
            override fun onSuccess(t: T) {
                super.onSuccess(t)
                onSuccess?.accept(t)
                liveData?.value = ApiResponse.Success(t)
            }

            override fun onFailure(requestException: RequestException) {
                super.onFailure(requestException)
                onFailure?.accept(requestException)
                liveData?.value = ApiResponse.Failure(requestException)
            }

            override fun showProgress(isShown: Boolean) {
                super.showProgress(isShown)
                liveData?.value = ApiResponse.Progress(isShown)
            }
        })
    }
}

class CompletableWrapper constructor(val completable : Completable) {
    fun subscribe(
        disposableContainer: CompositeDisposableContainer,
        observer: CompletableRequestObserver
    ) {
        completable.subscribe(observer)
        disposableContainer.getCompositeDisposable().add(observer.disposable)
    }

    fun subscribe(
        disposableContainer: CompositeDisposableContainer,
        liveData: MutableLiveData<ApiResponse<Boolean>>? = null,
        onComplete: Action? = null,
        onError: Consumer<in Throwable>? = null
    ) {
        subscribe(disposableContainer, object : CompletableRequestObserver() {
            override fun onComplete() {
                super.onComplete()
                liveData?.value = ApiResponse.Success(true)
                onComplete?.run()
            }

            override fun onFailure(requestException: RequestException) {
                super.onFailure(requestException)
                liveData?.value = ApiResponse.Failure(requestException)
                onError?.accept(requestException)
            }

            override fun showProgress(isShown: Boolean) {
                super.showProgress(isShown)
                liveData?.value = ApiResponse.Progress(isShown)
            }
        })
    }
}