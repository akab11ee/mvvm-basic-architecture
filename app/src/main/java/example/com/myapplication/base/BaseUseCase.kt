package example.com.myapplication.base

import example.com.myapplication.inteface.ThreadScheduler
import io.reactivex.Completable
import io.reactivex.CompletableTransformer
import io.reactivex.Single
import io.reactivex.SingleTransformer
import javax.inject.Inject

open class SingleUseCase @Inject constructor(private val threadScheduler: ThreadScheduler):BaseUseCase(){

    private fun <T> applySingleSchedulers(): SingleTransformer<T, T> {
        return SingleTransformer {
            it.subscribeOn(threadScheduler.io())
                .observeOn(threadScheduler.main())
        }
    }

    fun <T> buildUseCase(f: () -> Single<T>): Single<T> {
        return f().compose(applySingleSchedulers())
    }
}

open class CompletableUseCase @Inject constructor(private val threadScheduler: ThreadScheduler):BaseUseCase(){

    private fun applySingleSchedulers(): CompletableTransformer {
        return CompletableTransformer {
            it.subscribeOn(threadScheduler.io())
                .observeOn(threadScheduler.main())
        }
    }

    fun buildUseCase(f: () -> Completable): Completable {
        return f().compose(applySingleSchedulers())
    }
}

open class SingleWrapperUseCase @Inject constructor(private val threadScheduler: ThreadScheduler):BaseUseCase(){

    private fun <T> applySingleSchedulers(): SingleTransformer<T, T> {
        return SingleTransformer {
            it.subscribeOn(threadScheduler.io())
                .observeOn(threadScheduler.main())
        }
    }

    fun <T:Any> buildUseCase(f: () -> Single<T>): SingleWrapper<T> {
        return SingleWrapper(f().compose(applySingleSchedulers()))

    }
}

open class CompletableWrapperUseCase @Inject constructor(private val threadScheduler: ThreadScheduler):BaseUseCase(){
    private fun applyCompletableSchedulers(): CompletableTransformer {
        return CompletableTransformer {
            it.subscribeOn(threadScheduler.io())
                .observeOn(threadScheduler.main())
        }
    }

    fun buildUseCase(f: () -> Completable): CompletableWrapper {
        return CompletableWrapper(f().compose(applyCompletableSchedulers()))

    }
}



abstract class BaseUseCase