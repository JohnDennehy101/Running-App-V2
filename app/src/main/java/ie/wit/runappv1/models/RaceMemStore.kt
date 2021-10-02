package ie.wit.runappv1.models
import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class RaceMemStore : RaceStore {

    val races = ArrayList<RaceModel>()

    override fun findAll(): List<RaceModel> {
        return races
    }

    override fun create(race: RaceModel) {
        race.id = getId()
        races.add(race)
        logAll()
    }

    override fun update(race: RaceModel) {
        var foundRace: RaceModel? = races.find { p -> p.id == race.id }
        if (foundRace != null) {
            foundRace.title = race.title
            foundRace.description = race.description
            foundRace.raceDate = race.raceDate
            logAll()
        }
    }

    private fun logAll() {
       races.forEach { i("$it") }
    }
}