package ie.wit.runappv2.ui.maplist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ie.wit.runappv2.R
import ie.wit.runappv2.databinding.FragmentMapListBinding
import ie.wit.runappv2.models.RaceModel
import androidx.lifecycle.Observer


class MapListFragment : Fragment(), OnMapReadyCallback {

    private var _fragBinding: FragmentMapListBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var races : MutableList<RaceModel>
    private lateinit var map: GoogleMap

    private lateinit var mapListViewModel: MapListViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        races = mutableListOf<RaceModel>()

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentMapListBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        mapListViewModel = ViewModelProvider(this).get(MapListViewModel::class.java)
        mapListViewModel.observableRacesList.observe(viewLifecycleOwner, Observer {
                races ->
            races?.let { updateRaceValues(races) }
        })

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return root
    }

    fun updateRaceValues (racesList: List<RaceModel>) {
        races = racesList.toMutableList()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }


}