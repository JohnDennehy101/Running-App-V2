package ie.wit.runappv1.models
import android.os.Environment
import ie.wit.runappv1.helpers.*
import timber.log.Timber.i
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import android.content.Context
import java.io.File
import java.util.*


val JSON_FILE =  "races.json"

val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<RaceModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class RaceJSONMemStore : RaceJSONStore {
    val context: Context

    var races = mutableListOf<RaceModel>()

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
        val jsonString = gsonBuilder.toJson(races, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        races = Gson().fromJson(jsonString, listType)
    }
}