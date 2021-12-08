package ie.wit.runappv2.ui.map

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
import ie.wit.runappv2.databinding.FragmentMapBinding
import ie.wit.runappv2.ui.race.RaceFragmentArgs

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    private var _fragBinding: FragmentMapBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var map: GoogleMap
    val args: RaceFragmentArgs by navArgs()
    var location = Location()

    override fun onCreate(savedInstanceState: Bundle?) {

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


}