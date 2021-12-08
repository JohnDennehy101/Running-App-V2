package ie.wit.runappv2.ui.race

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.runappv2.firebase.FirebaseDBManager
import ie.wit.runappv2.models.RaceModel

class RaceViewModel : ViewModel()  {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addRace(race: RaceModel) {
        status.value = try {
            FirebaseDBManager.uploadRace(race)
            //RaceJSONMemStore.create(race)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun updateRace(race: RaceModel) {
        try {
            FirebaseDBManager.updateRace(race)
           //RaceJSONMemStore.update(race)
        } catch (e: IllegalArgumentException) {}


    }
}