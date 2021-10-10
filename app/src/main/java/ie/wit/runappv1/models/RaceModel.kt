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
    var image: String = ""
//    var image: Uri = "about:blank".toUri()
) : Parcelable