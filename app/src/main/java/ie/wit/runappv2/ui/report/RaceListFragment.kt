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
import android.graphics.Color
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
import android.widget.ImageView
import android.widget.ToggleButton
import androidx.fragment.app.activityViewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import ie.wit.runappv2.ui.auth.LoggedInViewModel
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar


class RaceListFragment : Fragment(), RaceListener  {
    private var _fragBinding: FragmentReportBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var raceListViewModel: RaceListViewModel
    lateinit var loader : AlertDialog
    var currentUserEmail : String = ""
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    var menuSwitch : Switch? = null

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

        Loader().showLoader(loader, "Downloading Races")

        raceListViewModel = ViewModelProvider(this).get(RaceListViewModel::class.java)

        fragBinding.filterFunctionalityCard.visibility = View.GONE


        raceListViewModel.racesListLiveData.observe(viewLifecycleOwner, Observer {
                races ->
            races?.let {
                render(races as ArrayList<RaceModel>)
                Loader().hideLoader(loader)
                checkSwipeRefresh()
            }
        })

        fragBinding.toggleAllRacesButtonGroup.addOnButtonCheckedListener {
            toggleButtonGroup, checkedId, isChecked ->
            if (isChecked) {

                when (checkedId) {
                    R.id.allRacesToggleButton -> {

                        fragBinding.racesNotFound.visibility = View.GONE
                        fragBinding.toggleFavouritesButtonGroup.clearChecked()
                        fragBinding.toggleFavouritesButtonGroup.check(R.id.allRaceRecordsButton)
                        fragBinding.toggleFavouritesButtonGroup.visibility = View.INVISIBLE
                        raceListViewModel.load()
                    }
                    R.id.userRacesToggleButton -> {
                        raceListViewModel.getRacesCreatedByCurrentUser()
                        fragBinding.toggleFavouritesButtonGroup.visibility = View.VISIBLE
                    }

                }

            }
        }

        fragBinding.toggleFavouritesButtonGroup.addOnButtonCheckedListener {
                toggleButtonGroup, checkedId, isChecked ->
            if (isChecked) {

                when (checkedId) {
                    R.id.allRaceRecordsButton -> raceListViewModel.getRacesCreatedByCurrentUser()
                    R.id.userFavouritesButton -> raceListViewModel.getUserFavourites()
                }

            }
        }

        setSwipeRefresh()

        fragBinding.recyclerView.setLayoutManager(LinearLayoutManager(activity))

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val race : RaceModel = viewHolder.itemView.tag as RaceModel
                Loader().showLoader(loader,"Deleting Race")
                val adapter = fragBinding.recyclerView.adapter as RaceAdapter

                if(race.createdUser == raceListViewModel.liveFirebaseUser.value?.uid!!) {
                    adapter.removeAt(viewHolder.adapterPosition)

                    raceListViewModel.delete(race.uid.toString())
                }
                else {
                    Snackbar.make(requireView(), "Cannot delete other user's races.",
                        Snackbar.LENGTH_SHORT).show()
                    raceListViewModel.load()
                }

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
        if (activity != null && requireActivity().isFinishing()) {
            Loader().showLoader(loader,"Downloading Races")
        }

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

        menuSwitch =
            menu.findItem(R.id.switch_action_bar).actionView.findViewById(R.id.menuSwitch) as Switch


        menuSwitch!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {

                fragBinding.filterFunctionalityCard.visibility = View.VISIBLE
            }
            else {
                fragBinding.filterFunctionalityCard.visibility = View.INVISIBLE
                fragBinding.filterFunctionalityCard.visibility = View.GONE
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
                    menuSwitch?.visibility = View.GONE
                    menuSwitch?.isChecked = false
                    fragBinding.filterFunctionalityCard.visibility = View.GONE
                    raceListViewModel.filter(searchText)
                    fragBinding.recyclerView.adapter?.notifyDataSetChanged()
                }
                else {
                    menuSwitch?.visibility = View.VISIBLE
                    raceListViewModel.load()
                }
                return false
            }

        })
    }

    override fun onRaceClick(race: RaceModel) {
        val editRaceAction = RaceListFragmentDirections.actionReportFragmentToRaceFragment(race)
        if(race.createdUser == raceListViewModel.liveFirebaseUser.value?.uid!!) {
            requireView().findNavController().navigate(editRaceAction)
        }
        else {
            Snackbar.make(requireView(), "Cannot edit other user's races.",
                Snackbar.LENGTH_SHORT).show()

            raceListViewModel.load()
        }

    }

    override fun onRaceDeleteClick(race: RaceModel) {

        if(race.createdUser == raceListViewModel.liveFirebaseUser.value?.uid!!) {
            raceListViewModel.delete(race.uid.toString())
            requireView().findNavController().run {
                popBackStack()
                navigate(R.id.reportFragment)
            }
        }

        else {
            Snackbar.make(requireView(), "Cannot delete other user's races.",
                Snackbar.LENGTH_SHORT).show()

            raceListViewModel.load()
        }




    }

    override fun onRaceFavouriteClick(race: RaceModel, favButton: ToggleButton) {
        raceListViewModel.setRaceFavouriteState(race, favButton.isChecked)
        fragBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { fragBinding.recyclerView.adapter?.notifyDataSetChanged() }
    }

    private fun render(racesList: ArrayList<RaceModel>) {
        fragBinding.recyclerView.adapter = RaceAdapter(racesList, this, raceListViewModel.liveFirebaseUser.value?.uid!!)
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