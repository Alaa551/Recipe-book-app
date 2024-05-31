package com.example.recipe_book_app.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe_book_app.R
import com.example.recipe_book_app.databinding.ActivityCategoriesBinding
import com.example.recipe_book_app.database.Database
import com.example.recipe_book_app.database.model.CategoryModel
import com.example.recipe_book_app.ui.adapter.RecyclerViewAdapter
import com.example.recipe_book_app.ui.listners.DeleteListner
import com.example.recipe_book_app.ui.listners.RecyclerOnItemClick
import java.util.*
import kotlin.collections.ArrayList

class Categories : AppCompatActivity() , RecyclerOnItemClick, DeleteListner {
   private lateinit var  binding: ActivityCategoriesBinding
    private  var db: Database = Database(this)
    private var  categories:MutableList<CategoryModel> ?=null
 companion object{
      lateinit var recyclerViewAdapter: RecyclerViewAdapter

 }


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //nav bar
        binding.bottomNavbar.selectedItemId= R.id.category
        binding.bottomNavbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> startActivity(Intent(this, HomePage::class.java))

                R.id.category -> print("categories")

            }
            return@setOnItemSelectedListener true
        }


    // add new Category
        binding.addFab.setOnClickListener {
            startActivity(Intent(this, AddNewCategory::class.java))
        }
        //remove add from user
          removeFloatingButton()


      categories= db.getAllCategories()

        recyclerViewAdapter = RecyclerViewAdapter(categories!!, this,this,getSharedPreferences("mySharedPref",
            MODE_PRIVATE
        ),this)

            val lm: RecyclerView.LayoutManager = LinearLayoutManager(this)

            binding.categories.setHasFixedSize(true)
            binding.categories.layoutManager = lm
            binding.categories.adapter = recyclerViewAdapter

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
    }





private fun filter(text: String) {
    // creating a new array list to filter our data.
    val filteredlist: ArrayList<CategoryModel> = ArrayList()

    // running a for loop to compare elements.
    if (categories!=null) {
        for (item in categories!!) {
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
        recyclerViewAdapter.filterList(filteredlist)
    }
}
    private fun removeFloatingButton(){
        if(getSharedPreferences("mySharedPref",Context.MODE_PRIVATE).getInt("user_type",-1)==1){
            binding.addFab.visibility= View.VISIBLE
        }
        else{
            binding.addFab.visibility=View.GONE
        }

    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, RecipesOfCategory::class.java)
        intent.putExtra("category_id", categories!![position].id)
        startActivity(intent)
    }

    override fun delete(position: Int) {
        recyclerViewAdapter.deleteCategory(position)
    }


}