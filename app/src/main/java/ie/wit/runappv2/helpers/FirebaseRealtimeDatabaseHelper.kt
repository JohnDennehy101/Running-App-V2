package ie.wit.runappv2.helpers
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import ie.wit.runappv2.models.RaceModel
import java.util.*
import com.google.firebase.database.DataSnapshot




class FirebaseRealtimeDatabaseHelper {
    private val mDatabaseRef = FirebaseDatabase.getInstance("https://runningappv1-default-rtdb.europe-west1.firebasedatabase.app").getReference("races")
    private var list: ArrayList<RaceModel> = ArrayList()


    fun uploadRace (race: RaceModel) : RaceModel {

        mDatabaseRef.child(race.id.toString()).setValue(race).addOnSuccessListener {

        }.addOnFailureListener {

        }
        return race

    }
    fun getUploadedRaces(liveData : MutableLiveData<List<RaceModel>>) : ArrayList<RaceModel> {

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
        return list
    }

    fun getFilteredRaces(liveData : MutableLiveData<List<RaceModel>>, searchText : String) : ArrayList<RaceModel> {
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
        return racesList
    }

    fun deleteRace (id : String) {
        mDatabaseRef.child(id).removeValue()
    }

    fun updateRace (race : RaceModel) {
        mDatabaseRef.child(race.id.toString()).setValue(race)
    }
}