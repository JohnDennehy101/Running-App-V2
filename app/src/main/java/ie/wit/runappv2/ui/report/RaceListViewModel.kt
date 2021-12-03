package ie.wit.runappv2.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.runappv2.helpers.FirebaseRealtimeDatabaseHelper
import ie.wit.runappv2.models.RaceJSONMemStore
import ie.wit.runappv2.models.RaceModel
import timber.log.Timber
import java.lang.Exception

class RaceListViewModel : ViewModel() {

    private val firebaseDbHelper = FirebaseRealtimeDatabaseHelper()


    private val _racesListLiveData = MutableLiveData<List<RaceModel>>()
    val racesListLiveData : LiveData<List<RaceModel>> = _racesListLiveData

    init {
        load()
    }

    fun load() {
        firebaseDbHelper.getUploadedRaces(_racesListLiveData)
    }

    fun delete(id: String) {
        try {
            firebaseDbHelper.deleteRace(id)
            //RaceJSONMemStore.delete(id)
            Timber.i("Firebase Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Firebase Delete Error : $e.message")
        }
    }
}