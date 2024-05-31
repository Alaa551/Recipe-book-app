package com.example.recipe_book_app.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe_book_app.R
import com.example.recipe_book_app.auth.UserController
import com.example.recipe_book_app.databinding.ActivityHomePageBinding
import com.example.recipe_book_app.database.Database
import com.example.recipe_book_app.database.model.RecipeModel
import com.example.recipe_book_app.ui.adapter.RecipeRecyclerViewAdapter
import com.example.recipe_book_app.ui.listners.DeleteListner
import com.example.recipe_book_app.ui.listners.RecyclerOnItemClick
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.collections.ArrayList

class HomePage : AppCompatActivity(), RecyclerOnItemClick, DeleteListner {
    private lateinit var binding: ActivityHomePageBinding

    private var db: Database = Database(this)
     private var recipes: MutableList<RecipeModel>? = null
    private lateinit var locale: Locale
    private var currentLanguage = "en"
    private lateinit var userController: UserController
  companion object{
      lateinit var recipeRecyclerViewAdapter: RecipeRecyclerViewAdapter
  }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //update direction of language
        currentLanguage = intent.getStringExtra("currentLang").toString()
        if (currentLanguage == "ar") {

            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {

            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }



//(remove add from user)
        removeFloatingButton()


        //nav bar
        binding.bottomNavbar.selectedItemId = R.id.home
        binding.bottomNavbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> print("home")

                R.id.category -> {
                    intent = Intent(this, Categories::class.java)
                    startActivity(intent)

                }
            }
            return@setOnItemSelectedListener true
        }

        // add recipe
        binding.addFabHome.setOnClickListener {
            val categories=(db.getAllCategoriesNames())
            if(categories != null && categories.isNotEmpty())
                startActivity(Intent(this, AddNewRecipe::class.java))

            else
                Toast.makeText(this,"There is no categories to add in it, please add category first",Toast.LENGTH_SHORT).show()
        }


        // recycler view recipe 1

        recipes = db.getAllRecipes()

        recipeRecyclerViewAdapter = RecipeRecyclerViewAdapter(recipes!!, this, this,getSharedPreferences("mySharedPref",
            MODE_PRIVATE
        ),this)

        val lm: RecyclerView.LayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        binding.recipe1Recyclerview.setHasFixedSize(true)
        binding.recipe1Recyclerview.layoutManager = lm
        binding.recipe1Recyclerview.adapter = recipeRecyclerViewAdapter

        // search
        binding.searchView.clearFocus()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
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

        // filter spinner
        val arrangment = resources.getStringArray(R.array.filter)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item, arrangment
        )
        binding.filter.adapter = adapter

        binding.filter.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    1 -> filterTime("ascending")
                    2 -> filterTime("descending")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

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
        val filteredList: ArrayList<RecipeModel> = ArrayList()

        // running a for loop to compare elements.
        if (recipes != null) {
            for (item in recipes!!) {
                // checking if the entered string matched with any item of our recycler view.
                if (item.title.lowercase(Locale.ROOT).contains(text.lowercase(Locale.ROOT))) {
                    // if the item is matched we are
                    // adding it to our filtered list.
                    filteredList.add(item)
                }
            }
        }
        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            recipeRecyclerViewAdapter.filterList(filteredList)
        }
    }

    fun filterTime(sortType: String) {
        if (sortType == "ascending") {
            recipes?.sortBy {
                it.time
            }
        } else {
            recipes?.sortByDescending {
                it.time
            }
        }


    }

    // app bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.light -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            R.id.dark -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            R.id.arabic -> {

                setLocale("ar")

            }
            R.id.english -> {

                setLocale("en")
            }
            R.id.rate_us -> {
                rate()

            }
            R.id.logout -> {
                userController = UserController(this)
                userController.logout()
            }
        }
        return true
    }

    // set language
    private fun setLocale(localeName: String) {
        if (localeName != currentLanguage) {
            locale = Locale(localeName)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.locale = locale
            res.updateConfiguration(conf, dm)
            val refresh = Intent(
                this,
                HomePage::class.java
            )
            refresh.putExtra("currentLang", localeName)
            startActivity(refresh)
        } else {
            Toast.makeText(
                this, "Language already selected)!", Toast.LENGTH_SHORT
            ).show()
        }


     }

    // rate system
    private fun rate() {
        val inflater = LayoutInflater.from(this)
        val v = inflater.inflate(R.layout.rate_system, null)
        val dialog = MaterialAlertDialogBuilder(this)
        dialog.setView(v)
        val rate: AppCompatRatingBar = v.findViewById(R.id.rate)
        dialog.setPositiveButton("Send") { dialog, _ ->
            Toast.makeText(this, "Rate is ${rate.rating}", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        dialog.create().show()
    }


private fun removeFloatingButton(){
    if(getSharedPreferences("mySharedPref",Context.MODE_PRIVATE).getInt("user_type",-1)==1){
        binding.addFabHome.visibility= View.VISIBLE
    }
    else{
        binding.addFabHome.visibility=View.GONE
    }

}



    override fun delete(position: Int) {
        recipeRecyclerViewAdapter.deleteRecipe(position)
    }
}







