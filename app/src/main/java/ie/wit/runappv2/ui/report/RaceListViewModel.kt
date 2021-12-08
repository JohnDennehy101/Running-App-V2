package ie.wit.runappv2.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.runappv2.models.RaceModel
import timber.log.Timber
import java.lang.Exception
import ie.wit.runappv2.firebase.FirebaseDBManager

class RaceListViewModel : ViewModel() {


    private val _racesListLiveData = MutableLiveData<List<RaceModel>>()
    val racesListLiveData : LiveData<List<RaceModel>> = _racesListLiveData

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    init {
        load()
    }

    fun load() {
        FirebaseDBManager.getUploadedRaces(_racesListLiveData)
    }

    fun filter(searchText : String) {
        FirebaseDBManager.getFilteredRaces(_racesListLiveData, searchText)
    }

    fun getRacesCreatedByCurrentUser (email : String) {
        FirebaseDBManager.getUserCreatedRaces(_racesListLiveData, liveFirebaseUser.value?.uid!!, email)
    }

    fun delete(raceId: String) {
        try {
            FirebaseDBManager.deleteRace(liveFirebaseUser.value?.uid!!, raceId)
            Timber.i("Firebase Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Firebase Delete Error : $e.message")
        }
    }
}