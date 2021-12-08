package ie.wit.runappv2.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class RaceModel(
    var uid: String? = "",
    var title: String = "",
    var description: String = "",
    var raceDate: String = "",
    var raceDistance: String = "",
    var image: String = "",
    var location: Location = Location(0.0,0.0,0f),
    var createdUser : String = "",
    var updatedUser : String = ""
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "title" to title,
            "description" to description,
            "raceDate" to raceDate,
            "raceDistance" to raceDistance,
            "image" to image,
            "location" to location,
            "createdUser" to createdUser,
            "updatedUser" to updatedUser
        )
    }
}



@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable