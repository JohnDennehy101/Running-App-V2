package ie.wit.runappv1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RaceModel(
    var id: Long = 0,
    var title: String = "",
    var description: String = "") : Parcelable