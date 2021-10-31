package ie.wit.runappv1.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.android.material.snackbar.Snackbar
import ie.wit.runappv1.R
import ie.wit.runappv1.main.MainApp
import ie.wit.runappv1.databinding.RegisterBinding
import ie.wit.runappv1.models.UserModel


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: RegisterBinding
    var user = UserModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        binding.registerButton.setOnClickListener() {
            user.userName = binding.userName.text.toString()
            user.email = binding.email.text.toString()

            println(binding.password.text.toString().isNotEmpty())


            if (binding.password.text.toString().isNotEmpty() && binding.password.text.toString() == binding.confirmPassword.text.toString()) {
                val passHash = BCrypt.withDefaults().hashToString(12, binding.password.text.toString().toCharArray())
                user.passwordHash = passHash.toCharArray()
            }
            else if (!binding.password.text.toString().isNotEmpty()) {
                Snackbar.make(it,"Please provide a password", Snackbar.LENGTH_LONG).show()
            }
            else {
                Snackbar.make(it,"Password field does not match confirmation password field, please try again.", Snackbar.LENGTH_LONG).show()
            }


               if (user.userName.length == 0) {
                   Snackbar.make(it,"Please provide a username", Snackbar.LENGTH_LONG).show()
               }
               else if (user.email.length == 0) {
                   Snackbar.make(it,"Please provide an email", Snackbar.LENGTH_LONG).show()
               }
               else if (user.passwordHash.toString().length == 0) {
                   Snackbar.make(it,"Please provide a password", Snackbar.LENGTH_LONG).show()
               }


                if (user.userName.length > 0 && user.email.length > 0 && user.passwordHash.isNotEmpty()) {
                    app.users.create(user.copy())
                    Snackbar.make(it,"User successfully created", Snackbar.LENGTH_LONG).show()
                    val i = Intent(this, RaceActivity::class.java)
                    setResult(RESULT_OK)
                    finish()
                    startActivity(i)
                }


        }

        binding.loginLink.setOnClickListener() {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
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




}