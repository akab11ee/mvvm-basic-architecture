package example.com.myapplication

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import com.nukecare.di.modules.ApplicationModule
import example.com.myapplication.base.ApplicationLevelAction
import example.com.myapplication.di.ApplicationComponent
import example.com.myapplication.di.DaggerApplicationComponent
import timber.log.Timber

class MyApplication : Application(), Application.ActivityLifecycleCallbacks {


    private var statusBarHeight = 0

    companion object {
        var currentActivityName: String? = null
        private var applicationLevelAction = ApplicationLevelAction()

        lateinit var mApplicationComponent: ApplicationComponent
            private set
        var isTerminated = true

        fun setApplicationLevelObject(applicationLevelActionObject: ApplicationLevelAction.ApplicationLevelActionObject) {
            applicationLevelAction.setApplicationLevelObject(applicationLevelActionObject)
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        mApplicationComponent = DaggerApplicationComponent.builder().applicationModule(
            ApplicationModule(this)
        ).build()


        initLibrariesInBackground()

        mApplicationComponent.inject(this)
        //Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)
    }


    fun rebuildDagger() {
        //mApplicationComponent = DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build()

    }


    private fun initLibrariesInBackground() {
        //Completable.create {
        //Fabric.with(this, Crashlytics())
//            it.onComplete()
//
//        }.subscribe(object : CompletableObserver {
//            override fun onComplete() {
//                Timber.d("initLibrariesInBackground completed")
//            }
//
//            override fun onSubscribe(d: Disposable) {}
//            override fun onError(e: Throwable) {}
//        })

    }

    private fun getActivityName(activity: Activity): String {
        var activityName = activity.localClassName
        val index = activityName.lastIndexOf(".")

        if (index != -1)
            activityName = activityName.substring(index + 1)

        return activityName
    }

    private fun initStatusBarHeight(decorView: View) {
        decorView.setOnApplyWindowInsetsListener { v, insets ->
            val sHeight = insets.systemWindowInsetTop // status bar height
            if (statusBarHeight == 0) {
                statusBarHeight = sHeight
            }
            insets
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Timber.d("Activity created %s", activity.javaClass)
        currentActivityName = getActivityName(activity)
        if (statusBarHeight == 0)
            initStatusBarHeight(activity?.window.decorView)

        // applicationLevelAction.inject(activity)
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        currentActivityName = getActivityName(activity)
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    @Synchronized
    override fun onActivityDestroyed(activity: Activity) {
        Timber.d("Activity destroyed %s", activity.javaClass)
        currentActivityName = null

    }
}