package ie.wit.runappv1.models

interface RaceStore {
    fun findAll(): List<RaceModel>
    fun create(race: RaceModel)
    fun update(race: RaceModel)
}