package ie.wit.runappv1.models

interface RaceJSONStore {
    fun findAll(): List<RaceModel>
    fun findOne(id: Long) : RaceModel?
    fun create(race: RaceModel)
    fun update(race: RaceModel)
}