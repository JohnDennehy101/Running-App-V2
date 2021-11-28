package ie.wit.runappv2.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.runappv2.models.RaceJSONMemStore
import ie.wit.runappv2.models.RaceModel

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
}