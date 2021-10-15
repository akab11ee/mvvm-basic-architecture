package example.com.myapplication.di

import example.com.myapplication.MyApplication


class Injector private constructor() {
    companion object {
        fun get(): ApplicationComponent = MyApplication.mApplicationComponent
    }
}