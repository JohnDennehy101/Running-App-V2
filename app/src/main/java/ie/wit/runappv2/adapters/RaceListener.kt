import android.widget.ToggleButton
import ie.wit.runappv2.models.RaceModel

interface RaceListener {
    fun onRaceClick(race: RaceModel)
    fun onRaceDeleteClick(race: RaceModel)
    fun onRaceFavouriteClick(race: RaceModel, favButton: ToggleButton)
}