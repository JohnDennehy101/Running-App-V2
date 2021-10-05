package ie.wit.runappv1.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import at.favre.lib.crypto.bcrypt.BCrypt
import ie.wit.runappv1.R
import ie.wit.runappv1.main.MainApp
import ie.wit.runappv1.databinding.LoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginBinding
//    private var requestCodeValue : Int = 55
//    var race = RaceModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.toolbarAdd.title = title
//        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        binding.loginButton.setOnClickListener() {
//            if (binding.userName.text.trim().isNotEmpty() && binding.password.text.trim()
//                    .isNotEmpty()
//            ) {

                val userCheck = app.users.findOne(binding.userName.text.toString())

                if (userCheck != null) {
                    var passwordHash = BCrypt.verifyer().verify(binding.password.text.trim().toString().toCharArray(), userCheck.passwordHash)

                    if (passwordHash.verified) {
                        val i = Intent(this, RaceListActivity::class.java)
                        setResult(RESULT_OK)
                        finish()
                        startActivity(i)
                    }
                    else {
                        Toast.makeText(this, "Password is incorrect", Toast.LENGTH_SHORT).show()
                    }
                }


//

//            } else {
//                Toast.makeText(this, "Username and password required", Toast.LENGTH_SHORT).show()
//            }
        }

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