package example.com.myapplication.inteface

import io.reactivex.Scheduler

interface ThreadScheduler {

    fun io(): Scheduler
    fun main(): Scheduler
    fun computation(): Scheduler

}