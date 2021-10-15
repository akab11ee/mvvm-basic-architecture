package com.nukecare.di.modules

import dagger.Module
import dagger.Provides
import example.com.myapplication.data.UserLoginDetail
import javax.inject.Singleton

@Module
@Suppress("unused")
object SessionModule {

    private var mUserLoginDetail = UserLoginDetail()

    @Provides
    @Singleton
    @JvmStatic
    fun provideUserDetails(): UserLoginDetail {
        return mUserLoginDetail
    }
}
