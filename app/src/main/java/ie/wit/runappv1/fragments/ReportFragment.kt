package ie.wit.runappv1.fragments

import RaceAdapter
import RaceListener
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.runappv1.R
import ie.wit.runappv1.databinding.FragmentReportBinding
import ie.wit.runappv1.main.MainApp
import ie.wit.runappv1.models.RaceModel
import java.util.*

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


        app = activity?.application as MainApp
        setHasOptionsMenu(true)

        races = app.races.findAll() as MutableList<RaceModel>

        filteredRaces = races.toMutableList()

        registerRefreshCallback()

        super.onCreate(savedInstanceState)




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _fragBinding = FragmentReportBinding.inflate(inflater, container, false)

        val root = fragBinding.root
        activity?.title = getString(R.string.action_report)

        fragBinding.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        fragBinding.recyclerView.adapter = RaceAdapter(filteredRaces, this)

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


    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
        val item = menu?.findItem(R.id.item_search)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredRaces.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())

                if (searchText.length > 0) {
                    races.forEach {

                        if (it.title.lowercase(Locale.getDefault()).contains(searchText.lowercase())) {
                            filteredRaces.add(it)
                        }

                    }
                    fragBinding.recyclerView.adapter?.notifyDataSetChanged()
                }
                else {
                    filteredRaces.clear()
                    filteredRaces.addAll(races)
                    fragBinding.recyclerView.adapter?.notifyDataSetChanged()
                }
                return false
            }

        })
    }

    override fun onRaceClick(race: RaceModel) {
        val editRaceAction = ReportFragmentDirections.actionReportFragmentToRaceFragment(race)
        requireView().findNavController().navigate(editRaceAction)
    }

    override fun onRaceDeleteClick(race: RaceModel) {

        app.races.delete(race)

        requireView().findNavController().run {
            popBackStack()
            navigate(R.id.reportFragment)
        }

    }
    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { fragBinding.recyclerView.adapter?.notifyDataSetChanged() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}