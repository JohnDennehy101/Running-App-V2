package ie.wit.runappv2.main

import android.app.Application
import ie.wit.runappv2.models.*
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    val race = RaceModel()

    lateinit var users: UserJSONStore

    lateinit var races: RaceJSONStore

    override fun onCreate() {
        super.onCreate()
        users = UserJSONMemStore(applicationContext, false)
        races = RaceJSONMemStore(applicationContext, false)
        Timber.plant(Timber.DebugTree())
        i("App started")
    }
}