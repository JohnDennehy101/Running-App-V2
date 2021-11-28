package ie.wit.runappv2.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.android.material.snackbar.Snackbar
import ie.wit.runappv2.R
import ie.wit.runappv2.main.MainApp
import ie.wit.runappv2.databinding.LoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginBinding
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        binding.loginButton.setOnClickListener() {

            //Rework with Firebase
                //val userCheck = app.users.findOne(binding.userName.text.toString())

               // if (userCheck != null) {
                    //var passwordHash = BCrypt.verifyer().verify(binding.password.text.trim().toString().toCharArray(), userCheck.passwordHash)

                    //if (passwordHash.verified) {
                        val i = Intent(this, RaceActivity::class.java)
                        //i.putExtra("user", userCheck)
                        setResult(RESULT_OK)
                        finish()
                        startActivity(i)
                    //}
                    //else {
                     //   Snackbar.make(it,"Incorrect password, please try again.", Snackbar.LENGTH_LONG).show()
                    //}
                }

               /* else if (binding.userName.text.toString().length == 0 && binding.password.text.toString().length == 0)
                {
                    Snackbar.make(it,"Please provide a username and password", Snackbar.LENGTH_LONG).show()
                }
                else {
                    Snackbar.make(it,"No user record found for that email", Snackbar.LENGTH_LONG).show()
                }*/

        //}

        binding.registerLink.setOnClickListener() {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }

    }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.menu_race, menu)
            return super.onCreateOptionsMenu(menu)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.item_cancel -> {
                    finish()
                }
            }
            return super.onOptionsItemSelected(item)
        }
}