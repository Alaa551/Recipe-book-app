package com.example.recipe_book_app.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe_book_app.R
import com.example.recipe_book_app.databinding.RecipeDetailsBinding
import com.example.recipe_book_app.database.Database
import com.example.recipe_book_app.database.DbBitmapUtility
import com.example.recipe_book_app.database.model.IngredientModel
import com.example.recipe_book_app.ui.adapter.IngredientRecyclerViewAdapter
import com.example.recipe_book_app.ui.listners.AddIngredientListner

class RecipeDetails : AppCompatActivity(), AddIngredientListner {
    private lateinit var binding: RecipeDetailsBinding
    private lateinit var ingredientRecyclerViewAdapter: IngredientRecyclerViewAdapter
    private var db: Database = Database(this)
    private val dbBitmapUtility: DbBitmapUtility = DbBitmapUtility()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ingredient and procedure buttons
        binding.ingrdientsButton.setOnClickListener {
            showIngredients()
            binding.ingrdientsButton.setBackgroundColor(getColor(R.color.darkGreen))
            binding.ingrdientsButton.setTextColor(getColor(R.color.white))
            binding.procedureButton.setBackgroundColor(getColor(R.color.white))
            binding.procedureButton.setTextColor(getColor(R.color.darkGreen))


        }
        binding.procedureButton.setOnClickListener {
            showProcedure()
            binding.procedureButton.setBackgroundColor(getColor(R.color.darkGreen))
            binding.procedureButton.setTextColor(getColor(R.color.white))
            binding.ingrdientsButton.setBackgroundColor(getColor(R.color.white))
            binding.ingrdientsButton.setTextColor(getColor(R.color.darkGreen))
        }


        //ingredient recycler view and details
        val details: Bundle? = intent.extras
        if (details != null) {
            binding.recipeDetailsImg.setImageBitmap(dbBitmapUtility.getImage(details.getByteArray("img")!!))
            binding.recipeDetailsTitle.text = details.getString("title")
            binding.procedure.text = details.getString("procedure")
            binding.recipeDetailsTime.text = "${details.getInt("time")} Mins"

        }

        //remove add from user
        removeFloatingButton()


        val recipeId = details!!.getInt("recipe_id")
        // add ingredient operation
        binding.addFab.setOnClickListener {
            val inflater = LayoutInflater.from(this)
            val v = inflater.inflate(R.layout.add_new_ingredient, null)
            val nameView: EditText? = v.findViewById(R.id.ingredient_name)
            val amontView: EditText? = v.findViewById(R.id.ingredient_amount)
            val addDialog = AlertDialog.Builder(this)
            addDialog.setView(v)
            addDialog.setPositiveButton("Save") { dialog, _ ->
                val ingredientName: String = nameView!!.text.toString()
                val ingredientAmout: String = amontView!!.text.toString()
                if (ingredientName.isNotEmpty() && ingredientAmout.isNotEmpty()) {
                    addIngredient(
                        IngredientModel(ingredientName, ingredientAmout.toInt()),
                        recipeId
                    )

                    Toast.makeText(this, "Ingredient added successfully", Toast.LENGTH_SHORT)
                        .show()
                }
                dialog.dismiss()

            }

            addDialog.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }


            addDialog.create().show()


        }

        val ingredients: MutableList<IngredientModel> = db.getAllRecipeIngredients(recipeId)

        // items count
        binding.numOfIngredients.text = "${ingredients.size} Items"

        ingredientRecyclerViewAdapter = IngredientRecyclerViewAdapter(ingredients, this)

        val lm: RecyclerView.LayoutManager = LinearLayoutManager(this)



        binding.ingredientsRecyclerview.setHasFixedSize(true)
        binding.ingredientsRecyclerview.layoutManager = lm
        binding.ingredientsRecyclerview.adapter = ingredientRecyclerViewAdapter


        // arrow back
        binding.arrowBack.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }

    }

    fun showIngredients() {
        binding.linearLayout3.visibility = View.VISIBLE
        binding.procedureLayout.visibility = View.GONE
    }

    fun showProcedure() {
        binding.linearLayout3.visibility = View.GONE
        binding.procedureLayout.visibility = View.VISIBLE
    }

    fun removeFloatingButton() {
        if (getSharedPreferences("mySharedPref", Context.MODE_PRIVATE).getInt(
                "user_type",
                -1
            ) == 1
        ) {
            binding.addFab.visibility = View.VISIBLE
        } else {
            binding.addFab.visibility = View.GONE
        }

    }

    override fun addIngredient(ingredientModel: IngredientModel, recipeId: Int) {
        ingredientRecyclerViewAdapter.addIngredient(ingredientModel, recipeId)
    }

}