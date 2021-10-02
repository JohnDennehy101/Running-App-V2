import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.runappv1.databinding.CardRaceBinding
import ie.wit.runappv1.models.RaceModel

class RaceAdapter constructor(private var races: List<RaceModel>, private val listener: RaceListener) :
    RecyclerView.Adapter<RaceAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardRaceBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val race = races[holder.adapterPosition]
        holder.bind(race, listener)
    }

    override fun getItemCount(): Int = races.size

    class MainHolder(private val binding : CardRaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(race: RaceModel, listener: RaceListener) {
            binding.raceTitle.text = race.title
            binding.raceDescription.text = race.description
            binding.raceDate.text = race.raceDate
            binding.raceDistance.text = race.raceDistance
            binding.root.setOnClickListener { listener.onRaceClick(race) }
        }
    }
}