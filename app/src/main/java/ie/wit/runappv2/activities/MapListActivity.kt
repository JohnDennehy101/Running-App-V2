package ie.wit.runappv2.activities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import ie.wit.runappv2.R
import ie.wit.runappv2.databinding.ActivityMapListBinding
import ie.wit.runappv2.main.MainApp
import ie.wit.runappv2.models.RaceModel

class MapListActivity : AppCompatActivity() {

    lateinit var app: MainApp
    private lateinit var races: List<RaceModel>

    private lateinit var binding: ActivityMapListBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        races = app.races.findAll()

        val navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)


    }
}