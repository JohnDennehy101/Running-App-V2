package ie.wit.runappv2.ui.report

import RaceAdapter
import RaceListener
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.app.AlertDialog
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
import ie.wit.runappv2.utils.*
import android.widget.Switch
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ie.wit.runappv2.ui.auth.LoggedInViewModel
import androidx.navigation.fragment.findNavController


class RaceListFragment : Fragment(), RaceListener  {
    private var _fragBinding: FragmentReportBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var raceListViewModel: RaceListViewModel
    lateinit var loader : AlertDialog
    var currentUserEmail : String = ""
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

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

        loader = Loader().createLoader(requireActivity())

        val root = fragBinding.root
        activity?.title = getString(R.string.action_report)

        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email!!

        Loader().showLoader(loader,"Downloading Races")

        raceListViewModel = ViewModelProvider(this).get(RaceListViewModel::class.java)



        raceListViewModel.racesListLiveData.observe(viewLifecycleOwner, Observer {
                races ->
            races?.let {
                render(races as ArrayList<RaceModel>)
                Loader().hideLoader(loader)
                checkSwipeRefresh()
            }
        })

        setSwipeRefresh()

        fragBinding.recyclerView.setLayoutManager(LinearLayoutManager(activity))

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Loader().showLoader(loader,"Deleting Race")
                val adapter = fragBinding.recyclerView.adapter as RaceAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                val race : RaceModel = viewHolder.itemView.tag as RaceModel
                raceListViewModel.delete(race.uid.toString())
                Loader().hideLoader(loader)
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

        val fab: FloatingActionButton = fragBinding.fab
        fab.setOnClickListener {
            val action = RaceListFragmentDirections.actionReportFragmentToRaceFragment()
            findNavController().navigate(action)
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RaceListFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    override fun onResume() {
        super.onResume()
        Loader().showLoader(loader,"Downloading Races")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser != null) {
                raceListViewModel.liveFirebaseUser.value = firebaseUser
                raceListViewModel.load()
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
        val item = menu?.findItem(R.id.item_search)

        val menuSwitch =
            menu.findItem(R.id.switch_action_bar).actionView.findViewById(R.id.menuSwitch) as Switch


        menuSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                raceListViewModel.getRacesCreatedByCurrentUser(currentUserEmail)
                fragBinding.recyclerView.adapter?.notifyDataSetChanged()
            }
            else {
                raceListViewModel.load()
                fragBinding.recyclerView.adapter?.notifyDataSetChanged()
            }
        })


        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchText = newText!!.lowercase(Locale.getDefault())

                if (searchText.length > 0) {
                    raceListViewModel.filter(searchText)
                    fragBinding.recyclerView.adapter?.notifyDataSetChanged()
                }
                else {
                    raceListViewModel.load()
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
        raceListViewModel.delete(race.uid.toString())

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

    private fun render(racesList: ArrayList<RaceModel>) {
        fragBinding.recyclerView.adapter = RaceAdapter(racesList, this)
        if (racesList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.racesNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.racesNotFound.visibility = View.GONE
            fragBinding.recyclerView.visibility = View.VISIBLE
        }
    }

    fun setSwipeRefresh() {
        fragBinding.swiperefresh.setOnRefreshListener {
            fragBinding.swiperefresh.isRefreshing = true
            Loader().showLoader(loader,"Downloading Races")
            raceListViewModel.load()
        }
    }

    fun checkSwipeRefresh() {
        if (fragBinding.swiperefresh.isRefreshing)
            fragBinding.swiperefresh.isRefreshing = false
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}