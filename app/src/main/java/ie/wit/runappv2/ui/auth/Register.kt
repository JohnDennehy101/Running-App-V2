package ie.wit.runappv2.ui.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ie.wit.runappv2.R
import ie.wit.runappv2.databinding.RegisterBinding
import ie.wit.runappv2.ui.home.Home
import timber.log.Timber
import androidx.lifecycle.Observer
import ie.wit.runappv2.helpers.ThemePreferenceHelper

class Register : AppCompatActivity() {

    private lateinit var loginRegisterViewModel : LoginRegisterViewModel
    private lateinit var registerBinding : RegisterBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = RegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        registerBinding.loginLink.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
        registerBinding.emailCreateAccountButton.setOnClickListener {
            createAccount(registerBinding.fieldEmail.text.toString(), registerBinding.fieldPassword.text.toString())
        }

        if (ThemePreferenceHelper(applicationContext).darkMode == 1) {
            registerBinding.relativeLayout.setBackgroundColor(Color.BLACK)
            registerBinding.icon.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_supervised_user_circle_24_night_mode))
            registerBinding.loginLink.setTextColor(Color.WHITE)
            registerBinding.loginLink.setBackgroundColor(Color.BLACK)
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        loginRegisterViewModel = ViewModelProvider(this).get(LoginRegisterViewModel::class.java)
        loginRegisterViewModel.liveFirebaseUser.observe(this, Observer
        { firebaseUser -> if (firebaseUser != null)
            startActivity(Intent(this, Home::class.java)) })

        loginRegisterViewModel.firebaseAuthManager.errorStatus.observe(this, Observer
        { status -> checkStatus(status) })
    }

    private fun checkStatus(error:Boolean) {
        if (error)
            Toast.makeText(this,
                getString(R.string.auth_failed),
                Toast.LENGTH_LONG).show()
    }

    private fun createAccount(email: String, password: String) {
        Timber.d("createAccount:$email")
        if (!validateForm()) { return }

        println("GETTING PAST FORM VALIDATION")
        loginRegisterViewModel.register(email,password)
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = registerBinding.fieldEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            registerBinding.fieldEmail.error = "Required."
            valid = false
        } else {
            registerBinding.fieldEmail.error = null
        }

        val password = registerBinding.fieldPassword.text.toString()
        val confirmPassWord = registerBinding.fieldConfirmPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            registerBinding.fieldPassword.error = "Required."
            valid = false
        }

        else if (TextUtils.isEmpty(confirmPassWord)) {
            registerBinding.fieldConfirmPassword.error = "Required."
            valid = false
        }

        else if (password == confirmPassWord) {
            registerBinding.fieldPassword.error = null
        }

        return valid
    }
}