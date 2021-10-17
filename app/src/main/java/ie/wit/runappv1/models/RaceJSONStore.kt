package ie.wit.runappv1.models

interface RaceJSONStore {
    fun findAll(): List<RaceModel>
    fun findOne(id: Long) : RaceModel?
    fun create(race: RaceModel, test: Boolean = false)
    fun update(race: RaceModel, test: Boolean = false)
    fun delete(race: RaceModel, test: Boolean = false)
}