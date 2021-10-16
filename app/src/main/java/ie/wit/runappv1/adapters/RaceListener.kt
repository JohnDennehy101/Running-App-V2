import ie.wit.runappv1.models.RaceModel

interface RaceListener {
    fun onRaceClick(race: RaceModel)
    fun onRaceDeleteClick(race: RaceModel)
}