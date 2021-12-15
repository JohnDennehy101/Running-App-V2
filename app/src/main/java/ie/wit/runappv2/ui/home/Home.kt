package ie.wit.runappv2.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseUser
import ie.wit.runappv2.R
import ie.wit.runappv2.databinding.HomeBinding
import ie.wit.runappv2.databinding.NavHeaderBinding
import ie.wit.runappv2.ui.auth.LoggedInViewModel
import ie.wit.runappv2.ui.auth.Login
import androidx.navigation.ui.setupWithNavController
import ie.wit.runappv2.firebase.FirebaseStorageManager
import ie.wit.runappv2.helpers.ThemePreferenceHelper
import ie.wit.runappv2.utils.readImageUri
import ie.wit.runappv2.utils.showImagePicker
import timber.log.Timber

class Home : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var homeBinding : HomeBinding
    private lateinit var navHeaderBinding : NavHeaderBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var loggedInViewModel : LoggedInViewModel
    private lateinit var headerView : View
    private lateinit var intentLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeBinding = HomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)

        drawerLayout = homeBinding.drawerLayout
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.reportFragment, R.id.raceFragment, R.id.mapFragment, R.id.mapListFragment), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView = homeBinding.navView
        navView.setupWithNavController(navController)

        initNavHeader()

    }

    public override fun onStart() {
        super.onStart()
        loggedInViewModel = ViewModelProvider(this).get(LoggedInViewModel::class.java)
        loggedInViewModel.liveFirebaseUser.observe(this, Observer { firebaseUser ->
            if (firebaseUser != null) {
               updateNavHeader(firebaseUser)
            }
        })

        loggedInViewModel.loggedOut.observe(this, Observer { loggedout ->
            if (loggedout) {
                startActivity(Intent(this, Login::class.java))
            }
        })

        registerImagePickerCallback()

    }

    private fun updateNavHeader(currentUser: FirebaseUser) {
        FirebaseStorageManager.imageUri.observe(this, { result ->
            if(result == Uri.EMPTY) {
                Timber.i("DX NO Existing imageUri")
                if (currentUser.photoUrl != null) {
                    //if you're a google user
                    FirebaseStorageManager.updateUserImage(
                        currentUser.uid,
                        currentUser.photoUrl,
                        navHeaderBinding.imageView,
                        false)
                }
                else
                {
                    Timber.i("DX Loading Existing Default imageUri")
                    FirebaseStorageManager.updateDefaultImage(
                        currentUser.uid,
                        R.drawable.ic_baseline_supervised_user_circle_24,
                        navHeaderBinding.imageView)
                }
            }
            else // load existing image from firebase
            {
                Timber.i("DX Loading Existing imageUri")
                FirebaseStorageManager.updateUserImage(
                    currentUser.uid,
                    FirebaseStorageManager.imageUri.value,
                    navHeaderBinding.imageView, false)
            }
        })
        navHeaderBinding.email.text = currentUser.email
        if(currentUser.displayName != null)
            navHeaderBinding.userName.text = currentUser.displayName
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun initNavHeader() {
        Timber.i("DX Init Nav Header")
        headerView = homeBinding.navView.getHeaderView(0)
        navHeaderBinding = NavHeaderBinding.bind(headerView)

        navHeaderBinding.imageView.setOnClickListener {
            showImagePicker(intentLauncher)
        }
    }

    private fun registerImagePickerCallback() {
        intentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("DX registerPickerCallback() ${readImageUri(result.resultCode, result.data).toString()}")
                            FirebaseStorageManager
                                .updateUserImage(loggedInViewModel.liveFirebaseUser.value!!.uid,
                                    readImageUri(result.resultCode, result.data),
                                    navHeaderBinding.imageView,
                                    true)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    fun signOut(item: MenuItem) {
        loggedInViewModel.logOut()
        val intent = Intent(this, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    fun changeTheme(item: MenuItem) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_theme_text))
        val styles = arrayOf("Light","Dark","System default")
        val checkedItem = ThemePreferenceHelper(this).darkMode

        builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->
            when (which) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    ThemePreferenceHelper(applicationContext).darkMode = 0
                    delegate.applyDayNight()
                    dialog.dismiss()
                    recreate()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    delegate.applyDayNight()
                    ThemePreferenceHelper(applicationContext).darkMode = 1
                    dialog.dismiss()
                    recreate()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    ThemePreferenceHelper(applicationContext).darkMode = 2
                    delegate.applyDayNight()
                    dialog.dismiss()
                    recreate()
                }

            }

        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun checkTheme() {
        when (ThemePreferenceHelper(applicationContext).darkMode) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.applyDayNight()
            }
        }
    }

}