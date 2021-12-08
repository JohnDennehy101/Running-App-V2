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

//        database.child(race.uid.toString()).setValue(race).addOnSuccessListener {
//
//        }.addOnFailureListener {
//
//        }


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

    override fun getUserCreatedRaces(liveData : MutableLiveData<List<RaceModel>>, userId: String, email : String) {
        val racesList = ArrayList<RaceModel>()

        database.child("user-races").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
                Log.e("Cancel", p0.toString())
            }
            override fun onDataChange (snapshot: DataSnapshot) {
                for (item in snapshot.children) {

                    val race : RaceModel = item.getValue(RaceModel::class.java)!!
                    if (race.createdUser.lowercase(Locale.getDefault()).equals(email.lowercase())) {
                        racesList.add(race)
                    }
                }

                database.child("user-races").child(userId)
                    .removeEventListener(this)
                liveData.postValue(racesList)
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
//        mDatabaseRef.child(race.id.toString()).child("title").setValue(race.title)
//        mDatabaseRef.child(race.id.toString()).child("description").setValue(race.description)
//        mDatabaseRef.child(race.id.toString()).child("image").setValue(race.image)
//        mDatabaseRef.child(race.id.toString()).child("location").setValue(race.location)
//        mDatabaseRef.child(race.id.toString()).child("raceDate").setValue(race.raceDate)
//        mDatabaseRef.child(race.id.toString()).child("raceDistance").setValue(race.raceDistance)
//        mDatabaseRef.child(race.id.toString()).child("updatedUser").setValue(race.updatedUser)

    }
}