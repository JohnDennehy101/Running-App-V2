import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.wit.runappv2.databinding.CardRaceBinding
import ie.wit.runappv2.models.RaceModel

class RaceAdapter constructor(private var races: ArrayList<RaceModel>, private val listener: RaceListener) :
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

    fun removeAt(position: Int) {
        races.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = races.size

    inner class MainHolder(val binding : CardRaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(race: RaceModel, listener: RaceListener) {
            //binding.root.tag = race._id
            binding.root.tag = race
            binding.raceTitle.text = race.title
            binding.raceDescription.text = race.description
            binding.raceDate.text = race.raceDate
            binding.raceDistance.text = race.raceDistance

            val params = LinearLayout.LayoutParams(
                200,
                200
            )
            binding.imageIcon.layoutParams = params
            binding.imageIcon.visibility = View.VISIBLE
            Picasso.get().setLoggingEnabled(true);

            Picasso.get().load(race.image.toUri()).resize(200,200).into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onRaceClick(race) }
            binding.deleteRaceButton.setOnClickListener {listener.onRaceDeleteClick(race)}
        }
    }
}