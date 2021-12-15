package ie.wit.runappv2.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.runappv2.models.RaceModel
import ie.wit.runappv2.models.RaceStore
import timber.log.Timber
import java.util.*

object FirebaseDBManager : RaceStore {
    private val database = FirebaseDatabase.getInstance("https://runningappv1-default-rtdb.europe-west1.firebasedatabase.app").reference
    private var list: ArrayList<RaceModel> = ArrayList()


    override fun createRace (firebaseUser: MutableLiveData<FirebaseUser>, race: RaceModel) {
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("races").push().key

        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }

        race.uid = key
        val raceValues = race.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/races/$key"] = raceValues
        childAdd["/user-races/$uid/$key"] = raceValues

        database.updateChildren(childAdd)


    }
    override fun getUploadedRaces(liveData : MutableLiveData<List<RaceModel>>) {

        database.child("races").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
                Log.e("Cancel", p0.toString())
            }
            override fun onDataChange (snapshot: DataSnapshot) {
                list = ArrayList<RaceModel>()
                val racesList : List<RaceModel> = snapshot.children.map { it ->
                    it.getValue(RaceModel::class.java)!!
                }

                liveData.postValue(racesList)
            }

        })
    }

    override fun getFilteredRaces(liveData : MutableLiveData<List<RaceModel>>, searchText : String) {
        val racesList = ArrayList<RaceModel>()

        database.child("races").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
                Log.e("Cancel", p0.toString())
            }
            override fun onDataChange (snapshot: DataSnapshot) {
                for (item in snapshot.children) {

                    val race : RaceModel = item.getValue(RaceModel::class.java)!!
                    if (race.title.lowercase(Locale.getDefault()).contains(searchText.lowercase())) {
                        racesList.add(race)
                    }
                }

                liveData.postValue(racesList)
            }

        })
    }

    override fun getUserCreatedRaces(liveData : MutableLiveData<List<RaceModel>>, userId: String) {
        val racesList = ArrayList<RaceModel>()

        database.child("user-races").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
                Log.e("Cancel", p0.toString())
            }
            override fun onDataChange (snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val race: RaceModel = item.getValue(RaceModel::class.java)!!
                        racesList.add(race)
                    }

                    database.child("user-races").child(userId)
                        .removeEventListener(this)
                    liveData.postValue(racesList)
                }
                else {
                    liveData.postValue(racesList)
                }
            }

        })
    }

    override fun deleteRace (userId: String, raceId: String) {

        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/races/$raceId"] = null
        childDelete["/user-races/$userId/$raceId"] = null

        database.updateChildren(childDelete)
    }

    override fun updateRace (userId : String, raceId : String, race : RaceModel) {

        val raceValues = race.toMap()

        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["races/$raceId"] = raceValues
        childUpdate["user-races/$userId/$raceId"] = raceValues

        database.updateChildren(childUpdate)

    }

    override fun setUserFavouriteRaceState (race : RaceModel, favouriteStatus : Boolean, firebaseUserId : String) {
        val uid = race.uid

        database.child("races").child(uid.toString()).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
                Log.e("Cancel", p0.toString())
            }
            override fun onDataChange (snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val firebaseRaceRecord : RaceModel = snapshot.getValue(RaceModel::class.java)!!
                    val checkFirebaseFavouritedUserIds = firebaseRaceRecord.favouritedBy.find {it == firebaseUserId}

                    println(checkFirebaseFavouritedUserIds)

                    if (favouriteStatus && checkFirebaseFavouritedUserIds == null) {
                        firebaseRaceRecord.favouritedBy.add(firebaseUserId)
                        println(firebaseRaceRecord)
                    }
                    else if (!favouriteStatus && checkFirebaseFavouritedUserIds != null) {
                        firebaseRaceRecord.favouritedBy.remove(firebaseUserId)
                    }

                    val childUpdate : MutableMap<String, Any?> = HashMap()
                    childUpdate["races/$uid"] = firebaseRaceRecord
                    database.updateChildren(childUpdate)
                    database.child("races").child(uid.toString())
                        .removeEventListener(this)
                }

            }

        })

        database.child("user-races").child(firebaseUserId).child(uid.toString()).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
                Log.e("Cancel", p0.toString())
            }
            override fun onDataChange (snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val firebaseRaceRecord : RaceModel = snapshot.getValue(RaceModel::class.java)!!
                    val checkFirebaseFavouritedUserIds = firebaseRaceRecord.favouritedBy.find {it == firebaseUserId}


                    if (favouriteStatus && checkFirebaseFavouritedUserIds == null) {
                        firebaseRaceRecord.favouritedBy.add(firebaseUserId)
                    }
                    else if (!favouriteStatus && checkFirebaseFavouritedUserIds != null) {
                        firebaseRaceRecord.favouritedBy.remove(firebaseUserId)
                    }

                    val childUpdate : MutableMap<String, Any?> = HashMap()
                    childUpdate["user-races/$firebaseUserId/$uid"] = firebaseRaceRecord
                    database.updateChildren(childUpdate)
                    database.child("user-races").child(firebaseUserId).child(uid.toString())
                        .removeEventListener(this)
                }

            }

        })
    }
    override fun getUserFavouritedRaces (liveData : MutableLiveData<List<RaceModel>>, userId: String) {
        val racesList = ArrayList<RaceModel>()

        database.child("races").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
                Log.e("Cancel", p0.toString())
            }
            override fun onDataChange (snapshot: DataSnapshot) {
                for (item in snapshot.children) {

                    val race : RaceModel = item.getValue(RaceModel::class.java)!!
                    if (race.favouritedBy.size > 0) {
                        for (id in race.favouritedBy) {
                            if (id == userId) {
                                racesList.add(race)
                            }
                        }
                    }
                }

                database.child("races")
                    .removeEventListener(this)
                liveData.postValue(racesList)
            }

        })
    }
}