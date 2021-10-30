package ie.wit.runappv1.helpers
import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import java.time.LocalDateTime
import java.util.*

fun limitRange(): CalendarConstraints.Builder {

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