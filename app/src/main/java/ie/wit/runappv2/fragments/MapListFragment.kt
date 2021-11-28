package ie.wit.runappv2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ie.wit.runappv2.R
import ie.wit.runappv2.databinding.FragmentMapListBinding
import ie.wit.runappv2.main.MainApp
import ie.wit.runappv2.models.RaceModel


/**
 * A simple [Fragment] subclass.
 * Use the [mapListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapListFragment : Fragment(), OnMapReadyCallback {

    private var _fragBinding: FragmentMapListBinding? = null
    private val fragBinding get() = _fragBinding!!
    //private lateinit var races : MutableList<RaceModel>
    lateinit var app: MainApp
    private lateinit var map: GoogleMap



    override fun onCreate(savedInstanceState: Bundle?) {
        app = activity?.application  as MainApp

        //races = app.races.findAll() as MutableList<RaceModel>

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentMapListBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapListFragment().apply {

            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val loc = LatLng(53.534046, -7.599592)

        /*for (race in races) {
            val locationCoordinates = LatLng(race.location.lat, race.location.lng)
            val options = MarkerOptions()
                .title(race.title)
                .snippet("GPS : $locationCoordinates")
                .draggable(false)
                .position(locationCoordinates)
            map.addMarker(options)

        }*/
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 6.5F))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}