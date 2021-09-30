package ie.wit.runappv1.models

class RaceMemStore : RaceStore {

    val races = ArrayList<RaceModel>()

    override fun findAll(): List<RaceModel> {
        return races
    }

    override fun create(race: RaceModel) {
        races.add(race)
    }
}