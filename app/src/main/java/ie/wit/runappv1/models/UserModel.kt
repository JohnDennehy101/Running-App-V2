package ie.wit.runappv1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.*

@Parcelize
data class UserModel(
    var userName: String = "",
    var email: String = "",
    var passwordHash: CharArray = charArrayOf()
) : Parcelable