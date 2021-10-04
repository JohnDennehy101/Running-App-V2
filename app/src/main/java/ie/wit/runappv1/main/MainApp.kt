package ie.wit.runappv1.main

import android.app.Application
import ie.wit.runappv1.models.RaceMemStore
import ie.wit.runappv1.models.UserMemStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    val races = RaceMemStore()
    val users = UserMemStore()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("App started")
    }
}