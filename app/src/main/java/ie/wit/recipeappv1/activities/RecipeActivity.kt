package ie.wit.recipeappv1.activities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ie.wit.recipeappv1.main.MainApp
import ie.wit.recipeappv1.databinding.ActivityRecipeBinding

class RecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeBinding
    //var placemark = PlacemarkModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

    }
}