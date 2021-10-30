package ie.wit.runappv1.activities


import RaceAdapter
import RaceListener
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.*
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView

import ie.wit.runappv1.databinding.ActivityRaceListBinding
import ie.wit.runappv1.main.MainApp

import ie.wit.runappv1.R
import ie.wit.runappv1.models.RaceModel
import java.util.*


class RaceListActivity : AppCompatActivity(), RaceListener {

    lateinit var app: MainApp
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var races : MutableList<RaceModel>
    private lateinit var filteredRaces : MutableList<RaceModel>
    private lateinit var binding: ActivityRaceListBinding
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaceListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
//        binding.toolbar.title = title
//        setSupportActionBar(binding.toolbar)

        app = application as MainApp
        races = app.races.findAll() as MutableList<RaceModel>

        filteredRaces = races.toMutableList()



        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = RaceAdapter(filteredRaces,this)

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
//        val navView : NavigationView = findViewById(R.id.nav_view)

//        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        val navView = binding.navView
        navView.setupWithNavController(navController)

        if (intent.hasExtra("race_delete")) {
            println("WORKING")
            Toast.makeText(this, "Deleted Race", Toast.LENGTH_LONG).show()
        }




//        navView.setNavigationItemSelectedListener {
//            val mapListIntent = Intent(this, MapListActivity::class.java)
//            when(it.itemId) {
////                R.id.item_home -> Toast.makeText(applicationContext, "Clicked home", Toast.LENGTH_SHORT).show()
//                R.id.item_map -> startActivity(mapListIntent)
//                R.id.item_logout -> Toast.makeText(applicationContext, "Clicked home", Toast.LENGTH_SHORT).show()
//            }
//            true
//        }

        registerRefreshCallback()


    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (toggle.onOptionsItemSelected(item)) {
//            return true
//        }
//        when (item.itemId) {
//            R.id.item_add -> {
//                val launcherIntent = Intent(this, RaceActivity::class.java)
//                refreshIntentLauncher.launch(launcherIntent)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val item = menu?.findItem(R.id.item_search)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredRaces.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())

                if (searchText.length > 0) {
                    races.forEach {

                        if (it.title.lowercase(Locale.getDefault()).contains(searchText.lowercase())) {
                            filteredRaces.add(it)
                        }

                    }
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                }
                else {
                    filteredRaces.clear()
                    filteredRaces.addAll(races)
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                }
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onRaceClick(race: RaceModel) {
        val launcherIntent = Intent(this, RaceActivity::class.java)
        launcherIntent.putExtra("race_edit", race)
        refreshIntentLauncher.launch(launcherIntent)
    }

    override fun onRaceDeleteClick(race: RaceModel) {
        val launcherIntent = Intent(this, RaceListActivity::class.java)
        launcherIntent.putExtra("race_delete", true)
        app.races.delete(race)
        refreshIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { binding.recyclerView.adapter?.notifyDataSetChanged() }
    }
}

