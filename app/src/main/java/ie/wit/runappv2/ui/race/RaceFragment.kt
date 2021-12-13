package ie.wit.runappv2.ui.race

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import ie.wit.runappv2.R
import ie.wit.runappv2.databinding.FragmentRaceBinding
import ie.wit.runappv2.firebase.FirebaseStorageManager
import ie.wit.runappv2.models.Location
import ie.wit.runappv2.models.RaceModel
import timber.log.Timber
import java.util.*
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ie.wit.runappv2.helpers.*
import ie.wit.runappv2.ui.auth.LoggedInViewModel

class RaceFragment : Fragment() {

    var race = RaceModel()
    private var _fragBinding: FragmentRaceBinding? = null
    private val fragBinding get() = _fragBinding!!
    var location = Location(52.245696, -7.139102, 7f)
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    val args: RaceFragmentArgs by navArgs()
    private lateinit var raceViewModel: RaceViewModel
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private lateinit var locationService: FusedLocationProviderClient
    lateinit var map: GoogleMap
    private var nightThemeCheck : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nightThemeCheck = checkTheme()
        registerImagePickerCallback()
        registerMapCallback()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _fragBinding = FragmentRaceBinding.inflate(inflater, container, false)
        val builder : MaterialDatePicker.Builder<Long> = MaterialDatePicker.Builder.datePicker()

        val editRace = args?.editRace


        locationService = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (checkLocationPermissions(requireContext()) || editRace != null) {
            setCurrentLocation()
        }
        else {
            var locationPermissionGranted = false
            val permissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { isGranted ->

                isGranted.entries.forEach {
                    if (it.value == true) {
                        locationPermissionGranted = true
                    }
                }

                if (locationPermissionGranted) {
                    setCurrentLocation()
                }

            }

            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))

        }

        fragBinding.mapView2.onCreate(savedInstanceState);
        fragBinding.mapView2.getMapAsync {
            map = it
            configureMap(map, editRace, nightThemeCheck)
        }

        val root = fragBinding.root
        activity?.title = getString(R.string.action_add_race)

        raceViewModel = ViewModelProvider(this).get(RaceViewModel::class.java)
        raceViewModel.observableStatus.observe(viewLifecycleOwner, Observer {
                status -> status?.let { render(status) }
        })

        val currentUser = FirebaseAuth.getInstance().currentUser


        if (editRace != null) {
            race = editRace
            location = editRace.location

            fragBinding.raceTitle.setText(race.title)
            fragBinding.raceDescription.setText(race.description)
            fragBinding.raceDatePicker.setText(race.raceDate)
            fragBinding.menuAutocomplete.setText(race.raceDistance)
            fragBinding.btnAdd.setText("Edit Race")

            if (race.image.length > 0) {
                Picasso.get().setLoggingEnabled(true);
                Picasso.get()
                    .load(race.image.toUri())
                    .into(fragBinding.imageView)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    800
                )
                fragBinding.imageView.layoutParams = params
                fragBinding.imageView.visibility = View.VISIBLE
            }
        }

        val items = listOf("1km", "5km", "8km", "10km", "Half Marathon (21km)", "Marathon (42km)")

        builder.setCalendarConstraints(
            limitRange().build()
        )



        val picker : MaterialDatePicker<Long> = builder.build()

        val count = Math.random()

        fragBinding.raceDatePicker.setOnClickListener {

            picker.show(requireFragmentManager(), count.toString())
        }

        picker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.time = Date(it)

            fragBinding.raceDatePicker.setText("${calendar.get(Calendar.DAY_OF_MONTH)}/" +
                    "${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}")
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.race_length_list_item, items)

        fragBinding.menuAutocomplete.setAdapter(adapter)

        val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val takenImage = data?.extras?.get("data") as Bitmap
                fragBinding.imageView.setImageBitmap(takenImage)
                val imageUri = takenImage.saveImage(requireActivity().applicationContext)

                if (imageUri != null) {
                    FirebaseStorageManager.uploadImage(imageUri, race)
                }

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    800
                )
                fragBinding.imageView.layoutParams = params
                fragBinding.imageView.visibility = View.VISIBLE

            }
        }

        fragBinding.takePictureButton.setOnClickListener() {
            Timber.i("Capture image")
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getImage.launch(takePictureIntent)
        }

        fragBinding.uploadPictureButton.setOnClickListener {
            Timber.i("Select image")
            showImagePicker(imageIntentLauncher)
        }

        fragBinding.raceLocation.setOnClickListener {
            Timber.i("Set Location Pressed")
            val setLocationAction = RaceFragmentDirections.actionRaceFragmentToMapFragment(location)
            requireView().findNavController().navigate(setLocationAction)
        }

        setAddOrUpdateRaceButtonListener(fragBinding, editRace, currentUser)






        return root
    }


    fun setAddOrUpdateRaceButtonListener (
        layout: FragmentRaceBinding,
        editRace: RaceModel?,
        currentUser: FirebaseUser?
    ) {


        layout.btnAdd.setOnClickListener() {
            race.title = fragBinding.raceTitle.text.toString()
            race.description = fragBinding.raceDescription.text.toString()
            race.raceDate = fragBinding.raceDatePicker.text.toString()
            race.raceDistance = fragBinding.menuAutocomplete.text.toString()
            race.location = location


            if (race.title.isNotEmpty() && race.description.isNotEmpty() && race.raceDate.isNotEmpty() && race.raceDistance.isNotEmpty() && editRace == null) {
                if (race.image.isEmpty()) {
                    race.image = "https://firebasestorage.googleapis.com/v0/b/runningappv1.appspot.com/o/images%2FSun%20Oct%2031%2016%3A52%3A53%20GMT%202021.png?alt=media&token=dec24aa1-37e6-423c-a002-6405ea9dcb97"
                }
                race.createdUser = loggedInViewModel.liveFirebaseUser.value?.uid!!
                raceViewModel.addRace(loggedInViewModel.liveFirebaseUser, race.copy())
                it.findNavController().navigate(R.id.action_raceFragment_to_reportFragment)
            }
            else if (race.title.isNotEmpty() && race.description.isNotEmpty() && race.raceDate.isNotEmpty() && race.raceDistance.isNotEmpty() && editRace != null) {
                race.updatedUser = loggedInViewModel.liveFirebaseUser.value?.uid!!
                raceViewModel.updateRace(loggedInViewModel.liveFirebaseUser.value?.uid!!, race.uid.toString(),race.copy())
                it.findNavController().navigate(R.id.action_raceFragment_to_reportFragment)
            }
            else if (race.title.isEmpty()) {
                Snackbar.make(it,"Please enter a title", Snackbar.LENGTH_LONG)
                    .show()
            }
            else if (race.description.isEmpty()) {
                Snackbar.make(it,"Please enter a description", Snackbar.LENGTH_LONG)
                    .show()
            }
            else if (race.raceDate.isEmpty()) {
                Snackbar.make(it,"Please provide a race date", Snackbar.LENGTH_LONG)
                    .show()
            }
            else if (race.raceDistance.isEmpty()) {
                Snackbar.make(it,"Please provide a race distance", Snackbar.LENGTH_LONG)
                    .show()
            }
        }

    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Result ${result.data!!.data}")
                            race.image = result.data!!.data!!.toString()

                            FirebaseStorageManager.uploadImage(result.data!!.data!!, race)

                            Picasso.get()
                                .load(race.image)
                                .into(fragBinding.imageView)
                            fragBinding.imageView.visibility = View.VISIBLE
                        } // end of if
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    //Uncomment this if you want to immediately return to Report
                   //navController.popBackStack()
                }
            }
            false -> Toast.makeText(context,getString(R.string.raceError), Toast.LENGTH_LONG).show()
        }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Location ${result.data.toString()}")
                            location = result.data!!.extras?.getParcelable("location")!!
                            Timber.i("Location == $location")
                        } // end of if
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun setCurrentLocation() {
        Timber.i("setting location from doSetLocation")
        locationService.lastLocation.addOnSuccessListener {
            location = Location(it.latitude, it.longitude)
        }
    }

    fun configureMap(m: GoogleMap, editRace: RaceModel?, nightThemeCheck: Boolean) {
        map = m
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
        locationUpdate(location.lat, location.lng, editRace)
    }

    private fun checkTheme() : Boolean {

        var nightThemeCheck : Boolean = false

        if (ThemePreferenceHelper(context).darkMode == 1) {
            nightThemeCheck = true
        }
        return nightThemeCheck
    }

    fun locationUpdate(lat: Double, lng: Double, editRace: RaceModel?) {
        map?.clear()
        map?.uiSettings?.setZoomControlsEnabled(true)
        var options : MarkerOptions?
        if (editRace != null) {
            options = MarkerOptions().title(editRace.title).position(LatLng(lat, lng))
        }
        else {
            options = MarkerOptions().position(LatLng(lat, lng))
        }

        map?.addMarker(options)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 15f))
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_race, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> { requireView().findNavController().navigate(R.id.reportFragment) }
        }
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        fragBinding.mapView2.onResume()
        val raceListViewModel = ViewModelProvider(this).get(RaceViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragBinding.mapView2.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        fragBinding.mapView2.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        fragBinding.mapView2.onPause()
    }

}