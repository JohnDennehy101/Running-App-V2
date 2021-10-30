package ie.wit.runappv1.fragments

import RaceAdapter
import RaceListener
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.runappv1.R
import ie.wit.runappv1.activities.RaceActivity
import ie.wit.runappv1.activities.RaceListActivity
import ie.wit.runappv1.databinding.FragmentReportBinding
import ie.wit.runappv1.main.MainApp
import ie.wit.runappv1.models.RaceModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportFragment : Fragment(), RaceListener  {
    private lateinit var filteredRaces : MutableList<RaceModel>
    private var _fragBinding: FragmentReportBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var races : MutableList<RaceModel>
    lateinit var app: MainApp
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = activity?.application as MainApp
        setHasOptionsMenu(true)

        races = app.races.findAll() as MutableList<RaceModel>

        filteredRaces = races.toMutableList()

        println("FILTERED REACES ")
        println(filteredRaces.size)

        registerRefreshCallback()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _fragBinding = FragmentReportBinding.inflate(inflater, container, false)

        val root = fragBinding.root

        val layoutManager = LinearLayoutManager(context)

//        fragBinding.test.setText("GREAST")
        fragBinding.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        fragBinding.recyclerView.adapter = RaceAdapter(filteredRaces, this)

//        fragBinding.recyclerView.layoutManager = layoutManager
//
//        fragBinding.recyclerView.adapter = RaceAdapter(filteredRaces,this)
        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReportFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReportFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onRaceClick(race: RaceModel) {
//        val launcherIntent = Intent(this, RaceActivity::class.java)
//        launcherIntent.putExtra("race_edit", race)
//        refreshIntentLauncher.launch(launcherIntent)
    }

    override fun onRaceDeleteClick(race: RaceModel) {
//        val launcherIntent = Intent(this, RaceListActivity::class.java)
//        launcherIntent.putExtra("race_delete", true)
//        app.races.delete(race)
//        refreshIntentLauncher.launch(launcherIntent)
    }
    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { fragBinding.recyclerView.adapter?.notifyDataSetChanged() }
    }
}