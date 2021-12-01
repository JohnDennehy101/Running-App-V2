package ie.wit.runappv2.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.runappv2.models.RaceJSONMemStore
import ie.wit.runappv2.models.RaceModel
import timber.log.Timber
import java.lang.Exception

class RaceListViewModel : ViewModel() {

    private val racesList = MutableLiveData<List<RaceModel>>()

    val observableRacesList: LiveData<List<RaceModel>>
        get() = racesList

    init {
        load()
    }

    fun load() {
        racesList.value = RaceJSONMemStore.findAll()
    }

    fun delete(id: String) {
        try {
            RaceJSONMemStore.delete(id)
            Timber.i("Firebase Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Firebase Delete Error : $e.message")
        }
    }
}