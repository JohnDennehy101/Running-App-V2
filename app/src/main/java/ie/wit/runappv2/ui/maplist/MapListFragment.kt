package ie.wit.runappv2.ui.maplist

import android.content.Context
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
import ie.wit.runappv2.R
import ie.wit.runappv2.databinding.FragmentMapListBinding
import ie.wit.runappv2.models.RaceModel
import androidx.lifecycle.Observer
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.model.*

import ie.wit.runappv2.helpers.ThemePreferenceHelper
import ie.wit.runappv2.ui.auth.LoggedInViewModel
import timber.log.Timber


class MapListFragment : Fragment(), OnMapReadyCallback {

    private var _fragBinding: FragmentMapListBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var races : MutableList<RaceModel>
    private lateinit var map: GoogleMap
    private var nightThemeCheck : Boolean = false

    private lateinit var mapListViewModel: MapListViewModel
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        races = mutableListOf<RaceModel>()

        nightThemeCheck = checkTheme()

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentMapListBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        mapListViewModel = ViewModelProvider(this).get(MapListViewModel::class.java)
        mapListViewModel.racesListLiveData.observe(viewLifecycleOwner, Observer {
                races ->
            races?.let { updateRaceValues(races) }
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        })




        return root
    }

    fun updateRaceValues (racesList: List<RaceModel>) {
        races = racesList.toMutableList()
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val loggedInUserId : String = loggedInViewModel.liveFirebaseUser.value?.uid!!

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

        val loc = LatLng(53.534046, -7.599592)

        for (race in races) {
            val locationCoordinates = LatLng(race.location.lat, race.location.lng)

            val userFavouriteRaceCheck = race.favouritedBy.find {it == loggedInUserId}

            if (race.createdUser == loggedInUserId && userFavouriteRaceCheck == null) {
                val options = MarkerOptions()
                    .title(race.title)
                    .snippet("GPS : $locationCoordinates")
                    .draggable(false)
                    .position(locationCoordinates)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                map.addMarker(options)
            }
            else if (userFavouriteRaceCheck != null) {
                val options = MarkerOptions()
                    .title(race.title)
                    .snippet("GPS : $locationCoordinates")
                    .draggable(false)
                    .position(locationCoordinates)
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_favourite_icon))
                map.addMarker(options)
            }
            else {
                val options = MarkerOptions()
                    .title(race.title)
                    .snippet("GPS : $locationCoordinates")
                    .draggable(false)
                    .position(locationCoordinates)
                map.addMarker(options)
            }



        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 6.5F))
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