package ie.wit.runappv1.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ie.wit.runappv1.R
import ie.wit.runappv1.databinding.ActivityMapListBinding
import ie.wit.runappv1.main.MainApp
import ie.wit.runappv1.models.Location
import ie.wit.runappv1.models.RaceModel

class MapListActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var app: MainApp
    private lateinit var map: GoogleMap
    private lateinit var races : List<RaceModel>

    private lateinit var binding: ActivityMapListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        races = app.races.findAll()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val loc = LatLng(53.534046, -7.599592)

        for (race in races) {
            val locationCoordinates = LatLng(race.location.lat, race.location.lng)
            val options = MarkerOptions()
                .title(race.title)
                .snippet("GPS : $locationCoordinates")
                .draggable(false)
                .position(locationCoordinates)
            map.addMarker(options)

        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 6.5F))
    }



    override fun onBackPressed() {
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
        super.onBackPressed()
    }
}