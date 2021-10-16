package ie.wit.runappv1.activities
import android.R.attr
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.datepicker.CalendarConstraints

import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import ie.wit.runappv1.R
import ie.wit.runappv1.main.MainApp
import ie.wit.runappv1.databinding.ActivityRaceBinding
import ie.wit.runappv1.models.RaceModel
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import android.R.attr.data
import android.net.Uri
import android.R.attr.data
import android.content.ContentValues
import android.content.Context
import android.os.*
import android.view.View
import android.widget.LinearLayout
import timber.log.Timber.i
import android.widget.RelativeLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import ie.wit.runappv1.helpers.FirebaseStorageManager
import ie.wit.runappv1.helpers.showImagePicker
import ie.wit.runappv1.models.Location
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream


class RaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRaceBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var race = RaceModel()
    var location = Location(52.245696, -7.139102, 15f)
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
        registerImagePickerCallback()

        app = application as MainApp

        val builder : MaterialDatePicker.Builder<Long> = MaterialDatePicker.Builder.datePicker()


        builder.setCalendarConstraints(
            limitRange().build()
        )



        if (intent.hasExtra("race_edit")) {
            race = intent.extras?.getParcelable("race_edit")!!
            location = race.location
            binding.raceTitle.setText(race.title)
            binding.raceDescription.setText(race.description)
            binding.raceDatePicker.setText(race.raceDate)
            binding.menuAutocomplete.setText(race.raceDistance)
            binding.btnAdd.setText("Edit Race")

            if (race.raceDate.substring(0,2).contains("/")) {
                race.raceDate = '0' + race.raceDate
            }


            var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            var date = LocalDateTime.parse(race.raceDate + " 01:00:01", formatter)
            val instant: Instant = date.atZone(ZoneId.systemDefault()).toInstant()
            val timeInMillis: Long = instant.toEpochMilli()


            builder.setSelection(timeInMillis)

            if (race.image.length > 0) {
                Picasso.get().setLoggingEnabled(true);
                Picasso.get()
                    .load(race.image.toUri())
                    .into(binding.imageView)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    800
                )
                binding.imageView.layoutParams = params
                binding.imageView.visibility = View.VISIBLE
            }
        }


        val picker : MaterialDatePicker<Long> = builder.build()

        val count = Math.random()

        binding.raceDatePicker.setOnClickListener {

            picker.show(supportFragmentManager, count.toString())
        }

        picker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.time = Date(it)

            binding.raceDatePicker.setText("${calendar.get(Calendar.DAY_OF_MONTH)}/" +
                    "${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}")
        }

        val items = listOf("1km", "5km", "8km", "10km", "Half Marathon (21km)", "Marathon (42km)")
        val adapter = ArrayAdapter(this, R.layout.race_length_list_item, items)

        binding.menuAutocomplete.setAdapter(adapter)


        binding.btnAdd.setOnClickListener() {
            race.title = binding.raceTitle.text.toString()
            race.description = binding.raceDescription.text.toString()
            race.raceDate = binding.raceDatePicker.text.toString()
            race.raceDistance = binding.menuAutocomplete.text.toString()
            race.location = location
            val i = Intent(this, RaceListActivity::class.java)



            if (race.title.isNotEmpty() && race.raceDate.isNotEmpty() && !intent.hasExtra("race_edit")) {
                app.races.create(race.copy())
                setResult(RESULT_OK)
                finish()
                startActivity(i)
            }
            else if (race.title.isNotEmpty() && intent.hasExtra("race_edit")) {
                app.races.update(race);
                setResult(RESULT_OK)
                finish()
                startActivity(i)
            }
            else {
                Snackbar.make(it,"Please Enter a title", Snackbar.LENGTH_LONG)
                    .show()
            }
        }


        val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val takenImage = data?.extras?.get("data") as Bitmap
                binding.imageView.setImageBitmap(takenImage)
                val imageUri = takenImage.saveImage(applicationContext)

                if (imageUri != null) {
                    FirebaseStorageManager().uploadImage(imageUri, race)
                }

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    800
                )
                binding.imageView.layoutParams = params
                binding.imageView.visibility = View.VISIBLE

            }
        }


        binding.takePictureButton.setOnClickListener() {
            i("Capture image")
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

//            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                getImage.launch(takePictureIntent)
           // }
        }

        binding.uploadPictureButton.setOnClickListener {
            i("Select image")
            showImagePicker(imageIntentLauncher)
        }



        binding.raceLocation.setOnClickListener {
            i ("Set Location Pressed")
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }

        registerMapCallback()





    }

    fun Bitmap.saveImage(context: Context): Uri? {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/running_app_pictures")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "img_${SystemClock.uptimeMillis()}")

            val uri: Uri? =
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(this, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
                return uri
            }
        } else {
            val directory =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + separator + "test_pictures")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName =  "img_${SystemClock.uptimeMillis()}"+ ".jpeg"
            val file = File(directory, fileName)
            saveImageToStream(this, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                return Uri.fromFile(file)
            }
        }
        return null
    }


    fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_race, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> { finish() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Location ${result.data.toString()}")
                            location = result.data!!.extras?.getParcelable("location")!!
                            i("Location == $location")
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            race.image = result.data!!.data!!.toString()

                            FirebaseStorageManager().uploadImage(result.data!!.data!!, race)

                            Picasso.get()
                                .load(race.image)
                                .into(binding.imageView)
                            binding.imageView.visibility = View.VISIBLE
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private fun limitRange(): CalendarConstraints.Builder {

        val constraintsBuilderRange = CalendarConstraints.Builder()

        val calendarStart: Calendar = GregorianCalendar.getInstance()
        val calendarEnd: Calendar = GregorianCalendar.getInstance()


        val currentDateTime = LocalDateTime.now()

        System.out.println(currentDateTime)


        val year = currentDateTime.toString().substring(0,4).toInt()
        val day = currentDateTime.toString().substring(8,10).toInt()
        val month = currentDateTime.toString().substring(5,7).toInt()


        calendarStart.set(year, month-1, day)
        calendarEnd.set(year + 1, 12, 31)

        val minDate = calendarStart.timeInMillis
        val maxDate = calendarEnd.timeInMillis

        constraintsBuilderRange.setStart(minDate)
        constraintsBuilderRange.setEnd(maxDate)

        constraintsBuilderRange.setValidator(RangeValidator(minDate, maxDate))

        return constraintsBuilderRange
    }

    class RangeValidator(private val minDate:Long, private val maxDate:Long) : CalendarConstraints.DateValidator{


        constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong()
        )

        override fun writeToParcel(dest: Parcel?, flags: Int) {
            TODO("not implemented")
        }

        override fun describeContents(): Int {
            TODO("not implemented")
        }

        override fun isValid(date: Long): Boolean {
            return !(minDate > date || maxDate < date)

        }

        companion object CREATOR : Parcelable.Creator<RangeValidator> {
            override fun createFromParcel(parcel: Parcel): RangeValidator {
                return RangeValidator(parcel)
            }

            override fun newArray(size: Int): Array<RangeValidator?> {
                return arrayOfNulls(size)
            }
        }

    }




}