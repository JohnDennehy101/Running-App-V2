package ie.wit.runappv2.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.runappv2.models.RaceModel
import ie.wit.runappv2.models.RaceStore
import java.util.*

object FirebaseDBManager : RaceStore {
    private val mDatabaseRef = FirebaseDatabase.getInstance("https://runningappv1-default-rtdb.europe-west1.firebasedatabase.app").getReference("races")
    private var list: ArrayList<RaceModel> = ArrayList()


    override fun createRace (race: RaceModel) {

        mDatabaseRef.child(race.id.toString()).setValue(race).addOnSuccessListener {

        }.addOnFailureListener {

        }


    }
    override fun getUploadedRaces(liveData : MutableLiveData<List<RaceModel>>) {

        mDatabaseRef.addValueEventListener(object : ValueEventListener {
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

        mDatabaseRef.addValueEventListener(object : ValueEventListener {
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

    override fun getUserCreatedRaces(liveData : MutableLiveData<List<RaceModel>>, email : String) {
        val racesList = ArrayList<RaceModel>()

        mDatabaseRef.addValueEventListener(object : ValueEventListener {
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

                liveData.postValue(racesList)
            }

        })
    }

    override fun deleteRace (id : String) {
        mDatabaseRef.child(id).removeValue()
    }

    override fun updateRace (race : RaceModel) {
        mDatabaseRef.child(race.id.toString()).child("title").setValue(race.title)
        mDatabaseRef.child(race.id.toString()).child("description").setValue(race.description)
        mDatabaseRef.child(race.id.toString()).child("image").setValue(race.image)
        mDatabaseRef.child(race.id.toString()).child("location").setValue(race.location)
        mDatabaseRef.child(race.id.toString()).child("raceDate").setValue(race.raceDate)
        mDatabaseRef.child(race.id.toString()).child("raceDistance").setValue(race.raceDistance)
        mDatabaseRef.child(race.id.toString()).child("updatedUser").setValue(race.updatedUser)

    }
}