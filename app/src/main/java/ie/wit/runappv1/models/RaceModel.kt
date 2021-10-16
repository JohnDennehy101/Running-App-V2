package ie.wit.runappv1.models

import android.net.Uri
import android.os.Parcelable
import androidx.core.net.toUri
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.*

@Parcelize
data class RaceModel(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var raceDate: String = "",
    var raceDistance: String = "",
    var image: String = "",
    var location: Location = Location(0.0,0.0,0f)
) : Parcelable



@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable