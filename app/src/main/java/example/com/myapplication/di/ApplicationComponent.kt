package example.com.myapplication.di


import com.nukecare.di.modules.ApplicationModule
import com.nukecare.di.modules.NetworkModule
import com.nukecare.di.modules.SessionModule
import example.com.myapplication.di.modules.ThreadSchedulerModule
import dagger.Component
import example.com.myapplication.MyApplication
import example.com.myapplication.base.ViewModelFactory
import example.com.myapplication.ui.NotesViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [SessionModule::class, NetworkModule::class, ApplicationModule::class, ThreadSchedulerModule::class])
interface ApplicationComponent {
    fun inject(nukeCareApplication: MyApplication)
    fun notesViewModelFactory(): ViewModelFactory<NotesViewModel>
}