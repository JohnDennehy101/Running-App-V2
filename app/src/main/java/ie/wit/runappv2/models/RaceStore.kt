package ie.wit.runappv2.models

import androidx.lifecycle.MutableLiveData

interface RaceStore {
    fun createRace (race: RaceModel)
    fun getUploadedRaces(liveData : MutableLiveData<List<RaceModel>>)
    fun getFilteredRaces(liveData : MutableLiveData<List<RaceModel>>, searchText : String)
    fun getUserCreatedRaces(liveData : MutableLiveData<List<RaceModel>>, email : String)
    fun deleteRace (id : String)
    fun updateRace (race : RaceModel)
}