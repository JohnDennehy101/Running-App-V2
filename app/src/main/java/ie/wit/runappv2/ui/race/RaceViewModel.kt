package ie.wit.runappv2.ui.race

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.runappv2.firebase.FirebaseDBManager
import ie.wit.runappv2.models.RaceModel

class RaceViewModel : ViewModel()  {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addRace(firebaseUser: MutableLiveData<FirebaseUser>, race: RaceModel) {
        status.value = try {
            FirebaseDBManager.createRace(firebaseUser, race)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun updateRace(userId: String, raceId : String, race: RaceModel) {
        try {
            FirebaseDBManager.updateRace(userId, raceId, race)
        } catch (e: IllegalArgumentException) {}


    }
}