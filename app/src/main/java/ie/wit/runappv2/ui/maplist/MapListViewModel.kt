package ie.wit.runappv2.ui.maplist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.runappv2.firebase.FirebaseDBManager
import ie.wit.runappv2.models.RaceModel
import timber.log.Timber
import java.lang.Exception

class MapListViewModel : ViewModel()  {

    private val _racesListLiveData = MutableLiveData<List<RaceModel>>()
    val racesListLiveData : LiveData<List<RaceModel>> = _racesListLiveData


    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    init {
        load()
    }

    fun load() {
        FirebaseDBManager.getUploadedRaces(_racesListLiveData)
    }

    fun getRacesCreatedByCurrentUser () {
        FirebaseDBManager.getUserCreatedRaces(_racesListLiveData, liveFirebaseUser.value?.uid!!)
    }
    fun getUserFavourites () {
        try {
            FirebaseDBManager.getUserFavouritedRaces(_racesListLiveData, liveFirebaseUser.value?.uid!!)
        }
        catch (e: Exception) {
            Timber.i("Firebase race favourite status update error : $e.message")
        }
    }
}