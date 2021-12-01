package ie.wit.runappv2.ui.report

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
import ie.wit.runappv2.R
import ie.wit.runappv2.databinding.FragmentReportBinding
import ie.wit.runappv2.models.RaceModel
import java.util.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ie.wit.runappv2.models.RaceJSONMemStore
import ie.wit.runappv2.utils.SwipeToDeleteCallback
import ie.wit.runappv2.utils.SwipeToEditCallback


class RaceListFragment : Fragment(), RaceListener  {
    private lateinit var filteredRaces : MutableList<RaceModel>
    private var _fragBinding: FragmentReportBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var races : MutableList<RaceModel>
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var raceListViewModel: RaceListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        races = mutableListOf<RaceModel>()
        filteredRaces = mutableListOf<RaceModel>()

        setHasOptionsMenu(true)

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

        raceListViewModel = ViewModelProvider(this).get(RaceListViewModel::class.java)
        raceListViewModel.observableRacesList.observe(viewLifecycleOwner, Observer {
                races ->
            races?.let { render(races as ArrayList<RaceModel>) }
            races?.let {updateRaceValues(races as ArrayList<RaceModel>)}
        })

        fragBinding.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        fragBinding.recyclerView.adapter = RaceAdapter(filteredRaces as ArrayList<RaceModel>, this)

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //showLoader(loader,"Deleting Donation")
                val adapter = fragBinding.recyclerView.adapter as RaceAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                raceListViewModel.delete(viewHolder.itemView.tag as String)
                //hideLoader(loader)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView)


        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onRaceClick(viewHolder.itemView.tag as RaceModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(fragBinding.recyclerView)

        return root
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
        val editRaceAction = RaceListFragmentDirections.actionReportFragmentToRaceFragment(race)
        requireView().findNavController().navigate(editRaceAction)
    }

    override fun onRaceDeleteClick(race: RaceModel) {

        //RaceJSONMemStore.delete(race)

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

    fun updateRaceValues (racesList: ArrayList<RaceModel>) {
        races = racesList.toMutableList()
        filteredRaces = racesList.toMutableList()
    }

    private fun render(racesList: ArrayList<RaceModel>) {
        fragBinding.recyclerView.adapter = RaceAdapter(racesList, this)
        filteredRaces = racesList.toMutableList()
        if (racesList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.racesNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.racesNotFound.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        raceListViewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}