package ie.wit.runappv2.ui.maplist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.runappv2.models.RaceModel

class MapListViewModel : ViewModel()  {

    private val racesList = MutableLiveData<List<RaceModel>>()


    val observableRacesList: LiveData<List<RaceModel>>
        get() = racesList

    init {
        load()
    }

    fun load() {
        //racesList.value = RaceJSONMemStore.findAll()
    }
}