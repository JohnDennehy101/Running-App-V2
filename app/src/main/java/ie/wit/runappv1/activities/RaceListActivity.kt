package ie.wit.runappv1.activities


import RaceAdapter
import RaceListener
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView

import ie.wit.runappv1.databinding.ActivityRaceListBinding
import ie.wit.runappv1.main.MainApp

import ie.wit.runappv1.R
import ie.wit.runappv1.models.RaceModel


class RaceListActivity : AppCompatActivity(), RaceListener {

    lateinit var app: MainApp
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityRaceListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaceListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

//        val layoutManager = LinearLayoutManager(this)
//        binding.recyclerView.layoutManager = layoutManager
//        binding.recyclerView.adapter = RaceAdapter(app.races.findAll(),this)


        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.item_home -> Toast.makeText(applicationContext, "Clicked home", Toast.LENGTH_SHORT).show()
                R.id.item_map -> Toast.makeText(applicationContext, "Clicked home", Toast.LENGTH_SHORT).show()
                R.id.item_logout -> Toast.makeText(applicationContext, "Clicked home", Toast.LENGTH_SHORT).show()
            }
            true
        }



//        val layoutManager = LinearLayoutManager(this)
//        binding.recyclerView.layoutManager = layoutManager
//        binding.recyclerView.adapter = RaceAdapter(app.races.findAll(),this)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, RaceActivity::class.java)
                startActivityForResult(launcherIntent,0)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onRaceClick(race: RaceModel) {
        val launcherIntent = Intent(this, RaceActivity::class.java)
        launcherIntent.putExtra("race_edit", race)
        startActivityForResult(launcherIntent,0)
    }
}

