import ie.wit.runappv2.models.RaceModel

interface RaceListener {
    fun onRaceClick(race: RaceModel)
    fun onRaceDeleteClick(race: RaceModel)
}