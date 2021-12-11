package ie.wit.runappv2.ui.map

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ie.wit.runappv2.R
import ie.wit.runappv2.databinding.FragmentMapListBinding
import ie.wit.runappv2.models.Location
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.MapStyleOptions
import ie.wit.runappv2.databinding.FragmentMapBinding
import ie.wit.runappv2.helpers.ThemePreferenceHelper
import ie.wit.runappv2.ui.race.RaceFragmentArgs
import timber.log.Timber

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    private var _fragBinding: FragmentMapBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var map: GoogleMap
    val args: RaceFragmentArgs by navArgs()
    var location = Location()
    private var nightThemeCheck : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        nightThemeCheck = checkTheme()
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentMapBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        location = args?.location!!

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (nightThemeCheck) {
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        context, ie.wit.runappv2.R.raw.style_night_map
                    )
                )
                if (!success) {
                    Timber.i("Styling parsing failed.")
                }
            } catch (e: Resources.NotFoundException) {
                Timber.i("Styling Not Found.")
            }
        }
        else {
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        context, ie.wit.runappv2.R.raw.style_day_map
                    )
                )
                if (!success) {
                    Timber.i("Styling parsing failed.")
                }
            } catch (e: Resources.NotFoundException) {
                Timber.i("Styling Not Found.")
            }
        }

        val loc = LatLng(location.lat, location.lng)
        val options = MarkerOptions()
            .title("Race")
            .snippet("GPS : $loc")
            .draggable(true)
            .position(loc)
        map.addMarker(options)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, location.zoom))
        map.setOnMarkerDragListener(this)
    }

    override fun onMarkerDragStart(marker: Marker) {
    }

    override fun onMarkerDrag(marker: Marker) {
    }

    override fun onMarkerDragEnd(marker: Marker) {
        location.lat = marker.position.latitude
        location.lng = marker.position.longitude
        location.zoom = map.cameraPosition.zoom
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    private fun checkTheme() : Boolean {

        var nightThemeCheck : Boolean = false

        if (ThemePreferenceHelper(context).darkMode == 1) {
            nightThemeCheck = true
        }
        return nightThemeCheck
    }


}