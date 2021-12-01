package ie.wit.runappv2.helpers
import com.google.firebase.database.FirebaseDatabase
import ie.wit.runappv2.models.RaceModel
import java.util.*

class FirebaseRealtimeDatabaseHelper {
    private val mDatabaseRef = FirebaseDatabase.getInstance("https://runningappv1-default-rtdb.europe-west1.firebasedatabase.app").getReference("races")
    fun uploadRace (race: RaceModel) : RaceModel {

        val date = Date().toString()

        mDatabaseRef.child(date).setValue(race).addOnSuccessListener {

        }.addOnFailureListener {

        }
        return race

    }
}