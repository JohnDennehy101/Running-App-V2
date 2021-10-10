package ie.wit.runappv1.main

import android.app.Application
import ie.wit.runappv1.models.*
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

//    val races = RaceMemStore()
//    val races = RaceJSONMemStore(applicationContext)
    val race = RaceModel()
//    var users = UserMemStore()

    lateinit var users: UserJSONStore

    lateinit var races: RaceJSONStore

    override fun onCreate() {
//        race.title = "Test 1"
//        race.raceDistance="10km"
//        race.raceDate="10/10/2021"
//        race.description="Test"
//        race2.title = "Test 2"
//        race2.raceDistance="5km"
//        race2.raceDate="11/10/2021"
//        race2.description="Test2"
        super.onCreate()
        users = UserJSONMemStore(applicationContext)
        races = RaceJSONMemStore(applicationContext)
//        races.create(race.copy())
//        races.create(race2.copy())
        Timber.plant(Timber.DebugTree())
        i("App started")
        println(races)
    }
}