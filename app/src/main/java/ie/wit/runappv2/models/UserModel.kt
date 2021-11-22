package ie.wit.runappv2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var userName: String = "",
    var email: String = "",
    var passwordHash: CharArray = charArrayOf()
) : Parcelable