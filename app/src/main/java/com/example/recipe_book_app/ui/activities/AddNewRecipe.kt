package com.example.recipe_book_app.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.recipe_book_app.databinding.ActivityAddNewRecipeBinding
import com.example.recipe_book_app.database.Database
import com.example.recipe_book_app.database.DbBitmapUtility
import com.example.recipe_book_app.database.model.RecipeModel
import com.example.recipe_book_app.ui.listners.AddRecipeListner
import com.example.recipe_book_app.ui.listners.UpdateRecipeListner

class AddNewRecipe : AppCompatActivity() , AddRecipeListner, UpdateRecipeListner {
    private lateinit var binding: ActivityAddNewRecipeBinding
    private var db: Database = Database(this)
    private val dbBitmapUtility: DbBitmapUtility = DbBitmapUtility()
    var isUpdate:String?=null
  var selectedItem:String?=null
    val chooseImg = 10
    var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNewRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set spinner items
        val categoryiesMap =db.getAllCategoriesNames()
             val categoriesNames = (db.getAllCategoriesNames()?.values)?.toList()
              selectedItem = categoriesNames!![0]
             val adapter = ArrayAdapter(
                 this,
                 android.R.layout.simple_spinner_dropdown_item, categoriesNames
             )
             binding.categoriesSpinner.adapter = adapter
             binding.categoriesSpinner.onItemSelectedListener = object :
                 AdapterView.OnItemSelectedListener {
                 override fun onItemSelected(
                     parent: AdapterView<*>?,
                     view: View?,
                     position: Int,
                     id: Long
                 ) {
                     selectedItem = parent?.getItemAtPosition(position).toString()
                 }

                 override fun onNothingSelected(parent: AdapterView<*>?) {
                     TODO("Not yet implemented")
                 }

             }



// choose image
        binding.selectImg.setOnClickListener {
            chooseImage()
        }

        //update operation
        val position: Int? = intent.extras?.get("position") as Int?
       isUpdate= intent.extras?.getString("update")

        var oldTitle: String? = null
        var oldBitmap: Bitmap? = null
        var oldProcedure:String?= null
        var oldTime:Int?= null

        if (position != null && isUpdate == "update") {
            oldTitle = HomePage.recipeRecyclerViewAdapter.recipes[position].title
            oldProcedure = HomePage.recipeRecyclerViewAdapter.recipes[position].procedure
            oldTime = HomePage.recipeRecyclerViewAdapter.recipes[position].time
            val id = HomePage.recipeRecyclerViewAdapter.recipes[position].id

            oldBitmap =
                dbBitmapUtility.getImage(db.getRecipeImg(id)!!)
            appearPreviousData(position)
        }
        else{
            binding.spinnerLayout.visibility=View.VISIBLE
        }

        //Add operation
        binding.saveCategory.setOnClickListener {
            val title = binding.addRecipeTitle.text.toString()
            val procedure = binding.addRecipeProcedure.text.toString()
            val time= binding.addRecipeTime.text.toString()
            // when update operatin
            if(isUpdate!=null) {
                saveUpdateData(title, procedure, time, oldTitle, oldProcedure, oldTime, oldBitmap,position)
                return@setOnClickListener
            }
            if (bitmap != null && title.isNotEmpty() && procedure.isNotEmpty() && time.isNotEmpty()) {
                val img: ByteArray = dbBitmapUtility.getBytes(bitmap!!)!!
                val res = db.insertImg(img)
                val categoryId= getKeyByValue(categoryiesMap!!,selectedItem.toString())!!.toInt()
                Log.d("category id",categoryId.toString())
                if (res) {
                    addRecipe(RecipeModel(title,procedure,time.toInt(),dbBitmapUtility.getBytes(bitmap!!)!!),categoryId)
                    startActivity(Intent(this, HomePage::class.java))
                }
            }
            else{
                Toast.makeText(this, "please enter all fields", Toast.LENGTH_SHORT).show()

            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == chooseImg && data != null && data.data != null) {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
            binding.imagePreview.setImageBitmap(bitmap)
        }
    }

    fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, chooseImg)
    }

    override fun addRecipe(recipeModel: RecipeModel, categoryId:Int) {
        HomePage.recipeRecyclerViewAdapter.addRecipe(recipeModel,categoryId)
    }

    fun getKeyByValue(map: Map<Int, String>, value: String): Int? {
        for ((key, mapValue) in map) {
            if (mapValue == value) {
                return key
            }
        }
        return null
    }

    fun appearPreviousData(position: Int) {
        //   oldBitmap= (binding.imagePreview.drawable as BitmapDrawable).bitmap
        val existRecipe = HomePage.recipeRecyclerViewAdapter.recipes[position]
        binding.addRecipeTitle.setText(existRecipe.title)
        binding.addRecipeProcedure.setText(existRecipe.procedure)
        binding.addRecipeTime.setText(existRecipe.time.toString())
        bitmap = dbBitmapUtility.getImage(existRecipe.img)
        binding.imagePreview.setImageBitmap(bitmap)

        binding.categoriesSpinner.visibility=View.GONE
    }

    override fun update(position: Int, newRecipe: RecipeModel) {
        HomePage.recipeRecyclerViewAdapter.updateRecipe(position,newRecipe)
    }

    fun saveUpdateData(
        title: String,
        procedure: String,
        time: String,
        oldTitle: String?,
        oldProcedure: String?,
        oldTime: Int?,
        oldBitmap: Bitmap?,
        position: Int?
    ) {


            if(title.isNotEmpty() && procedure.isNotEmpty() && time.isNotEmpty()){
                if(title!=oldTitle || procedure!=oldProcedure || time!=(oldTime.toString()) || !oldBitmap!!.sameAs(bitmap)){
                    db.insertImg(dbBitmapUtility.getBytes(bitmap!!)!!)
                    update(position!!,
                        RecipeModel(title,procedure,time.toInt(),dbBitmapUtility.getBytes(bitmap!!)!!)
                    )
                }
                else{
                    Toast.makeText(this, "Nothing has changed", Toast.LENGTH_SHORT).show()

                }
                startActivity(Intent(this,HomePage::class.java))

            }

            else{
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()

            }

        }



}
