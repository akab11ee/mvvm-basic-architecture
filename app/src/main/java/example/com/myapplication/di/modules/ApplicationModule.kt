package com.nukecare.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import example.com.myapplication.MyApplication
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: MyApplication) {

    @Singleton
    @Provides
    fun provideApplication(): MyApplication {
        return application
    }

    @Provides
    @Singleton
    fun provideContext(application: MyApplication): Context {
        return application.applicationContext
    }


}