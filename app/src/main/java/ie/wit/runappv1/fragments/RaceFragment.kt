package ie.wit.runappv1.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import ie.wit.runappv1.R
import ie.wit.runappv1.activities.MapActivity
import ie.wit.runappv1.activities.RaceListActivity
import ie.wit.runappv1.databinding.FragmentRaceBinding
import ie.wit.runappv1.helpers.*
import ie.wit.runappv1.main.MainApp
import ie.wit.runappv1.models.Location
import ie.wit.runappv1.models.RaceModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import timber.log.Timber.i


class RaceFragment : Fragment() {

    var race = RaceModel()
    private var _fragBinding: FragmentRaceBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var app: MainApp
    var location = Location(52.245696, -7.139102, 7f)
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    val args: RaceFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerImagePickerCallback()
        app = activity?.application as MainApp
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

        val root = fragBinding.root
        activity?.title = getString(R.string.action_add_race)


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
                    FirebaseStorageManager().uploadImage(imageUri, race)
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
            i("Capture image")
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

//            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                getImage.launch(takePictureIntent)
           // }
        }

        fragBinding.uploadPictureButton.setOnClickListener {
            i("Select image")
            showImagePicker(imageIntentLauncher)
        }

        fragBinding.raceLocation.setOnClickListener {
            i ("Set Location Pressed")
            val launcherIntent = Intent(activity, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }


        fragBinding.btnAdd.setOnClickListener() {
            race.title = fragBinding.raceTitle.text.toString()
            race.description = fragBinding.raceDescription.text.toString()
            race.raceDate = fragBinding.raceDatePicker.text.toString()
            race.raceDistance = fragBinding.menuAutocomplete.text.toString()
            race.location = location
            val i = Intent(context, RaceListActivity::class.java)



            if (race.title.isNotEmpty() && race.raceDate.isNotEmpty() && editRace == null) {
                app.races.create(race.copy())
                it.findNavController().navigate(R.id.action_raceFragment_to_reportFragment)
            }
            else if (race.title.isNotEmpty() && editRace != null) {
                app.races.update(race);
                it.findNavController().navigate(R.id.action_raceFragment_to_reportFragment)
            }
            else {
                Snackbar.make(it,"Please Enter a title", Snackbar.LENGTH_LONG)
                    .show()
            }
        }



        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RaceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RaceFragment().apply {

            }
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            race.image = result.data!!.data!!.toString()

                            FirebaseStorageManager().uploadImage(result.data!!.data!!, race)

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

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Location ${result.data.toString()}")
                            location = result.data!!.extras?.getParcelable("location")!!
                            i("Location == $location")
                        } // end of if
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }
            }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragments, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}