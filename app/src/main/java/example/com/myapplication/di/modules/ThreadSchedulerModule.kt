package example.com.myapplication.di.modules

import dagger.Module
import dagger.Provides
import example.com.myapplication.inteface.ThreadScheduler
import example.com.myapplication.base.ThreadSchedulerProvider

@Module
class ThreadSchedulerModule {

    @Provides
    fun provideThreadScheduler(): ThreadScheduler {
        return ThreadSchedulerProvider()
    }
}