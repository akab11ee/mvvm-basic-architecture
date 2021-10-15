package example.com.myapplication.base

import example.com.myapplication.inteface.ThreadScheduler
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ThreadSchedulerProvider : ThreadScheduler {
    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun main(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun computation() = Schedulers.computation()

}