import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.runappv1.databinding.CardRaceBinding
import ie.wit.runappv1.models.RaceModel

class RaceAdapter constructor(private var races: List<RaceModel>) :
    RecyclerView.Adapter<RaceAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardRaceBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val recipe = races[holder.adapterPosition]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = races.size

    class MainHolder(private val binding : CardRaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(race: RaceModel) {
            //binding.placemarkTitle.text = placemark.title
            //binding.description.text = placemark.description
        }
    }
}