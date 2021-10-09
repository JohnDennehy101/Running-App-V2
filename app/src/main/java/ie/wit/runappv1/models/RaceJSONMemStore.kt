package ie.wit.runappv1.models
import android.os.Environment
import ie.wit.runappv1.helpers.*
import timber.log.Timber.i
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import android.content.Context
import java.io.File
import java.lang.reflect.Type
import java.util.*


val JSON_FILE =  "data.json"

val combinedDataModel = UnifiedModel()

val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<Any>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class RaceJSONMemStore : RaceJSONStore {
    val context: Context

    var races = mutableListOf<RaceModel>()
    var users = mutableListOf<UserModel>()



    constructor (context: Context) {
        this.context = context
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): List<RaceModel> {
        return races
    }

    override fun findOne(id: Long) : RaceModel? {
        var foundRace: RaceModel? = races.find { p -> p.id == id }
        return foundRace
    }

    override fun create(race: RaceModel) {
        race.id = generateRandomId()
        races.add(race)
        serialize()
    }

    override fun update(race: RaceModel) {
        var foundRace = findOne(race.id!!)
        if (foundRace != null) {
            foundRace.title = race.title
            foundRace.description = race.description
            foundRace.raceDate = race.raceDate
            foundRace.raceDistance = race.raceDistance
        }
        serialize()
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

        races = dataResponse.get(0).races!!
        users = dataResponse.get(0).users!!
    }
}