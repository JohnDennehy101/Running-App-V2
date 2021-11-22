package ie.wit.runappv2.helpers
import android.util.Log

import android.net.Uri

import com.google.firebase.storage.FirebaseStorage
import ie.wit.runappv2.models.RaceModel
import java.util.*

class FirebaseStorageManager {
    private val mStorageRef = FirebaseStorage.getInstance().reference
    fun uploadImage(imageFileUri: Uri, race: RaceModel) : RaceModel {

        val date = Date()
        val uploadTask = mStorageRef.child("images/${date}.png").putFile(imageFileUri)
        uploadTask.addOnSuccessListener {
            Log.e("Firebase", "Image Upload success")

            val uploadedURL = mStorageRef.child("images/${date}.png").downloadUrl

            uploadedURL.addOnCompleteListener {
                if (it.isSuccessful) {
                    val downloadUrl = it.result
                    Log.e("Firebase", downloadUrl.toString())
                    race.image = downloadUrl.toString()

                }


                }


        }.addOnFailureListener {
            Log.e("Firebase", "Image Upload fail")

        }
        return race

    }
}