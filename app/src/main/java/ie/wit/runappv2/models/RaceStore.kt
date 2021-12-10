package ie.wit.runappv2.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface RaceStore {
    fun createRace (firebaseUser: MutableLiveData<FirebaseUser>, race: RaceModel)
    fun getUploadedRaces(liveData : MutableLiveData<List<RaceModel>>)
    fun getFilteredRaces(liveData : MutableLiveData<List<RaceModel>>, searchText : String)
    fun getUserCreatedRaces(liveData : MutableLiveData<List<RaceModel>>, userId: String)
    fun deleteRace (userId: String, raceId: String)
    fun updateRace (userId : String, raceId : String, race : RaceModel)
    fun setUserFavouriteRaceState (race : RaceModel, favouriteStatus : Boolean, firebaseUserId : String)
    fun getUserFavouritedRaces (liveData : MutableLiveData<List<RaceModel>>, userId: String)
}