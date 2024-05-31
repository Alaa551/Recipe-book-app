package com.example.recipe_book_app.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe_book_app.databinding.ActivityRecipesCategoryBinding
import com.example.recipe_book_app.database.Database
import com.example.recipe_book_app.database.model.RecipeModel
import com.example.recipe_book_app.ui.adapter.RecipeRecyclerViewAdapter
import com.example.recipe_book_app.ui.listners.DeleteListner
import com.example.recipe_book_app.ui.listners.RecyclerOnItemClick
import com.example.recipe_book_app.ui.listners.UpdateRecipeListner
import java.util.*
import kotlin.collections.ArrayList

class RecipesOfCategory : AppCompatActivity() , RecyclerOnItemClick, UpdateRecipeListner,
    DeleteListner {
    private lateinit var binding: ActivityRecipesCategoryBinding
    private lateinit var recipeRecyclerViewAdapter: RecipeRecyclerViewAdapter
    private  var db: Database = Database(this)
    private var  recipes:MutableList<RecipeModel> ?=null

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            binding = ActivityRecipesCategoryBinding.inflate(layoutInflater)
            setContentView(binding.root)





            // recycler view recipe 1
           val intent= intent
             val categoryId= intent.getIntExtra("category_id",0)
            recipes=db.getAllRecipesOfCategory(categoryId)

            recipeRecyclerViewAdapter = RecipeRecyclerViewAdapter(recipes!!, this,this,getSharedPreferences("mySharedPref", MODE_PRIVATE),this)

            val lm: RecyclerView.LayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


            binding.recipesRecyclerView.setHasFixedSize(true)
            binding.recipesRecyclerView.layoutManager = lm
            binding.recipesRecyclerView.adapter = recipeRecyclerViewAdapter


            // arrow back
            binding.arrowBack.setOnClickListener {
                val intent = Intent(this, Categories::class.java)
                startActivity(intent)
            }

            // search
            binding.searchView.clearFocus()
            binding.searchView.setOnQueryTextListener( object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(msg: String): Boolean {
                    // inside on query text change method we are
                    // calling a method to filter our recycler view.
                    filter(msg)
                    return true
                }
            })

            // update operation

        }



    override fun onItemClick(position: Int) {
        val intent = Intent(this, RecipeDetails::class.java)
        val b = Bundle()
        b.putInt("recipe_id", recipes!![position].id)
        b.putString("title", recipes!![position].title)
        b.putString("procedure", recipes!![position].procedure)
        b.putInt("time", recipes!![position].time)
        b.putByteArray("img",recipes!![position].img)

        intent.putExtras(b)
        startActivity(intent)
    }

private fun filter(text: String) {
    // creating a new array list to filter our data.
    val filteredlist: ArrayList<RecipeModel> = ArrayList()

    // running a for loop to compare elements.
    if (recipes!=null) {
        for (item in recipes!!) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.title.lowercase(Locale.ROOT).contains(text.lowercase(Locale.ROOT))) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
    }
    if (filteredlist.isEmpty()) {
        // if no item is added in filtered list we are
        // displaying a toast message as no data found.
        Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
    } else {
        // at last we are passing that filtered
        // list to our adapter class.
        recipeRecyclerViewAdapter.filterList(filteredlist)
    }
}

    override fun update(position: Int, newRecipe: RecipeModel) {
        recipeRecyclerViewAdapter.updateRecipe(position,newRecipe)
    }

    override fun delete(position: Int) {
        recipeRecyclerViewAdapter.deleteRecipe(position)
    }


}
