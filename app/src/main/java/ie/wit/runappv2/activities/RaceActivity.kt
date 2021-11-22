package ie.wit.runappv2.activities
import androidx.appcompat.app.AppCompatActivity
import ie.wit.runappv2.R
import ie.wit.runappv2.main.MainApp
import ie.wit.runappv2.databinding.ActivityRaceBinding
import ie.wit.runappv2.models.RaceModel
import android.os.*
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import android.os.Bundle
import androidx.navigation.ui.setupWithNavController
import ie.wit.runappv2.models.UserModel


class RaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRaceBinding
    private lateinit var drawerLayout: DrawerLayout
    var race = RaceModel()
    lateinit var app: MainApp
    lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaceBinding.inflate(layoutInflater)

        setContentView(binding.root)
        drawerLayout = binding.drawerLayout
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        app = application as MainApp

        val navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        val navView = binding.navView

        val navigationView  = navView
        val headerView = navigationView.getHeaderView(0)

       if (intent.hasExtra("user")) {
           user =  intent.extras?.getParcelable("user")!!
       }
        val navUsername : TextView = headerView.findViewById(R.id.userName)
        val navEmail : TextView = headerView.findViewById(R.id.email)

        navUsername.setText(user.userName)
        navEmail.setText(user.email)

        navView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

}