package ie.wit.runappv1.activities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import ie.wit.runappv1.R
import ie.wit.runappv1.main.MainApp
import ie.wit.runappv1.databinding.ActivityRaceBinding
import ie.wit.runappv1.models.RaceModel

class RaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRaceBinding
    var race = RaceModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        if (intent.hasExtra("race_edit")) {
            race = intent.extras?.getParcelable("race_edit")!!
            binding.raceTitle.setText(race.title)
            binding.raceDescription.setText(race.description)
            binding.btnAdd.setText("Edit Race")
        }

        binding.btnAdd.setOnClickListener() {
            race.title = binding.raceTitle.text.toString()
            race.description = binding.raceDescription.text.toString()
            if (race.title.isNotEmpty() && !intent.hasExtra("race_edit")) {
                app.races.create(race.copy())
                setResult(RESULT_OK)
                finish()
            }
            else if (race.title.isNotEmpty() && intent.hasExtra("race_edit")) {
                app.races.update(race);
                setResult(RESULT_OK)
                finish()
            }
            else {
                Snackbar.make(it,"Please Enter a title", Snackbar.LENGTH_LONG)
                    .show()
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_race, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> { finish() }
        }
        return super.onOptionsItemSelected(item)
    }
}