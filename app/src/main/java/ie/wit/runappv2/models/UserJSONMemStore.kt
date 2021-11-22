package ie.wit.runappv2.models
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ie.wit.runappv2.helpers.exists
import ie.wit.runappv2.helpers.read
import ie.wit.runappv2.helpers.write
import timber.log.Timber.i
import java.lang.reflect.Type
import java.util.*

class UserJSONMemStore : UserJSONStore {

    var users =
        mutableListOf<UserModel>()
    var races =
        mutableListOf<RaceModel>()

    val combinedDataModel = UnifiedModel()

    val context: Context

    val listType = object : TypeToken<ArrayList<Any>>() {}.type

    constructor (context: Context, test: Boolean) {
        this.context = context
        if (!test) {
            if (exists(context, JSON_FILE)) {
                deserialize()
            }
        }
    }

    override fun findAll(): List<UserModel> {
        return users
    }

    override fun findOne(email : String ): UserModel? {
        var foundUser: UserModel? = users.find { p -> p.email == email }
        return foundUser
    }

    override fun create(user: UserModel, test: Boolean) {
        users.add(user)
        if (!test) {
            serialize()
        }

        logAll()
    }

    override fun update(user: UserModel, test: Boolean) {
        var foundUser: UserModel? = users.find { p -> p.email == user.email }
//        if (foundRace != null) {
//            foundRace.title = race.title
//            foundRace.description = race.description
//            foundRace.raceDate = race.raceDate
//            foundRace.raceDistance = race.raceDistance
//            logAll()
//        }
    }

     fun logAll() {
       users.forEach { i("$it") }
    }

    private fun serialize() {

        combinedDataModel.races = races
        combinedDataModel.users = users

        val combinedData = mutableListOf(combinedDataModel)

        val jsonString = gsonBuilder.toJson(combinedData, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        val collectionType: Type = object : TypeToken<List<UnifiedModel?>?>() {}.type
        val dataResponse : ArrayList<UnifiedModel> = Gson().fromJson(jsonString, collectionType)
        users = dataResponse.get(0).users!!
        races = dataResponse.get(0).races!!
    }
}