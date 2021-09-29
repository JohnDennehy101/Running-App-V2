package ie.wit.runappv1.adapters

import RaceAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.wit.runappv1.databinding.ActivityRaceListBinding
import ie.wit.runappv1.databinding.CardRaceBinding
import ie.wit.runappv1.main.MainApp
import ie.wit.runappv1.models.RaceModel
import android.view.Menu
import android.view.MenuItem
import ie.wit.runappv1.R
import ie.wit.runappv1.activities.RaceActivity


class RecipeListActivity : AppCompatActivity() {

    lateinit var app: MainApp
    private lateinit var binding: ActivityRaceListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaceListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = RaceAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, RaceActivity::class.java)
                startActivityForResult(launcherIntent,0)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

