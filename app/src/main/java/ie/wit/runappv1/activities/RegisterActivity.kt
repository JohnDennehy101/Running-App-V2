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
//        binding.toolbarAdd.title = title
//        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        binding.registerButton.setOnClickListener() {
//            if (binding.userName.text.trim().isNotEmpty() && binding.password.text.trim()
//                    .isNotEmpty() && binding.email.text.trim().isNotEmpty() && binding.password.text == binding.confirmPassword.text
//            ) {
                val passHash = BCrypt.withDefaults().hashToString(12, binding.password.text.toString().toCharArray())
                user.userName = binding.userName.text.toString()
                user.email = binding.email.text.toString()
                user.passwordHash = passHash

                app.users.create(user.copy())
                val i = Intent(this, RaceListActivity::class.java)
                setResult(RESULT_OK)
                finish()
                startActivity(i)


//            } else {
//                Toast.makeText(this, "Username and password required", Toast.LENGTH_SHORT).show()
//            }
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