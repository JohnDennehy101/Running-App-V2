package org.wit.placemark.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.wit.recipeappv1.databinding.ActivityRecipeListBinding
import ie.wit.recipeappv1.databinding.CardRecipeBinding
import ie.wit.recipeappv1.main.MainApp
import ie.wit.recipeappv1.models.RecipeModel

//import ie.wit.recipeappv1.models.PlacemarkModel

class RecipeListActivity : AppCompatActivity() {

    lateinit var app: MainApp
    private lateinit var binding: ActivityRecipeListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        //binding.recyclerView.adapter = PlacemarkAdapter(app.placemarks)
    }
}

class PlacemarkAdapter constructor(private var recipes: List<RecipeModel>) :
    RecyclerView.Adapter<PlacemarkAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardRecipeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val recipe = recipes[holder.adapterPosition]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipes.size

    class MainHolder(private val binding : CardRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: RecipeModel) {
            //binding.placemarkTitle.text = placemark.title
            //binding.description.text = placemark.description
        }
    }
}