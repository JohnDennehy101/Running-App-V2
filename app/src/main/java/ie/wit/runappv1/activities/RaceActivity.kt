package ie.wit.runappv1.activities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ie.wit.runappv1.R
import ie.wit.runappv1.main.MainApp
import ie.wit.runappv1.databinding.ActivityRaceBinding

class RaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRaceBinding
    //var placemark = PlacemarkModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

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