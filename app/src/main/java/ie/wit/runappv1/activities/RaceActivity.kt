package ie.wit.runappv1.activities
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
import com.google.android.material.snackbar.Snackbar
import ie.wit.runappv1.R
import ie.wit.runappv1.main.MainApp
import ie.wit.runappv1.databinding.ActivityRaceBinding
import ie.wit.runappv1.models.RaceModel
import java.time.Month
import java.util.*

class RaceActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityRaceBinding
    var race = RaceModel()
    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0
    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0
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
            binding.btnAdd.setText("Edit Race")
        }

        pickDate()

        binding.btnAdd.setOnClickListener() {
            race.title = binding.raceTitle.text.toString()
            race.description = binding.raceDescription.text.toString()
            if (race.title.isNotEmpty() && !intent.hasExtra("race_edit")) {
                app.races.create(race.copy())
                setResult(RESULT_OK)
                finish()
            }
            else if (race.title.isNotEmpty() && intent.hasExtra("race_edit")) {
                app.races.update(race);
                setResult(RESULT_OK)
                finish()
            }
            else {
                Snackbar.make(it,"Please Enter a title", Snackbar.LENGTH_LONG)
                    .show()
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

    private fun getDateTimeCalendar() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)

    }

    private fun pickDate() {
        binding.raceDatePickerButton.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(this,this,year,month,day).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month + 1
        savedYear = year

        getDateTimeCalendar()
        TimePickerDialog(this,this,hour,minute,true).show()

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        binding.test.setText("$savedDay + $savedMonth + $savedYear + $savedHour + $savedMinute")
    }


}