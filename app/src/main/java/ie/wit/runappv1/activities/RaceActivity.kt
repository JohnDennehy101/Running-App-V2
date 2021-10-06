package ie.wit.runappv1.activities
import android.R.attr
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
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
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout


class RaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRaceBinding
    private var requestCodeValue : Int = 55
    var race = RaceModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp



        if (intent.hasExtra("race_edit")) {
            race = intent.extras?.getParcelable("race_edit")!!
            binding.raceTitle.setText(race.title)
            binding.raceDescription.setText(race.description)
            binding.raceDatePicker.setText(race.raceDate)
            binding.menuAutocomplete.setText(race.raceDistance)
            binding.btnAdd.setText("Edit Race")
        }



        val builder : MaterialDatePicker.Builder<Long> = MaterialDatePicker.Builder.datePicker()


            builder.setCalendarConstraints(
                limitRange().build()
            )

        if (intent.hasExtra("race_edit")) {
            race = intent.extras?.getParcelable("race_edit")!!
            if (race.raceDate.substring(0,2).contains("/")) {
                race.raceDate = '0' + race.raceDate
            }

            var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            var date = LocalDateTime.parse(race.raceDate + " 01:00:01", formatter)
            val instant: Instant = date.atZone(ZoneId.systemDefault()).toInstant()
            val timeInMillis: Long = instant.toEpochMilli()


            builder.setSelection(timeInMillis)
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
//            race.raceDate = LocalDate.of(binding.raceDatePicker.text.substring(binding.raceDatePicker.text.length - 4).toInt(), binding.raceDatePicker.text.substring(3,5).toInt(), binding.raceDatePicker.text.substring(0,2).toInt())
            race.raceDate = binding.raceDatePicker.text.toString()
            race.raceDistance = binding.menuAutocomplete.text.toString()
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
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    800
                )
                binding.imageView.layoutParams = params
                binding.imageView.visibility = View.VISIBLE

            }
        }


        binding.takePictureButton.setOnClickListener() {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

//            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                getImage.launch(takePictureIntent)
           // }
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