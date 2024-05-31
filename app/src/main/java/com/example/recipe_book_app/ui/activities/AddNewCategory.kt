package com.example.recipe_book_app.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipe_book_app.databinding.AddNewCategoryBinding
import com.example.recipe_book_app.database.Database
import com.example.recipe_book_app.database.DbBitmapUtility
import com.example.recipe_book_app.database.model.CategoryModel
import com.example.recipe_book_app.ui.listners.AddCategoryListener
import com.example.recipe_book_app.ui.listners.UpdateListner

class AddNewCategory : AppCompatActivity(), AddCategoryListener, UpdateListner {
    private lateinit var binding: AddNewCategoryBinding
    private var db: Database = Database(this)
    private val dbBitmapUtility: DbBitmapUtility = DbBitmapUtility()

    val chooseImg = 10
    var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddNewCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // add new category
        binding.selectImg.setOnClickListener {
            chooseImage()
        }

        // update operation
        val position: Int? = intent.extras?.getInt("position")
        val isUpdate = intent.extras?.getString("update")


        var oldTitle: String? = null
        var oldBitmap: Bitmap? = null
        if (position != null && isUpdate == "update") {
            oldTitle = Categories.recyclerViewAdapter.categories[position].title
            val id = Categories.recyclerViewAdapter.categories[position].id
            oldBitmap =
                dbBitmapUtility.getImage(db.getCategoryImg(id)!!)
            appearPreviousData(position!!)
        }

        binding.saveCategory.setOnClickListener {
            val title = binding.categoryTitle.text.toString()
            if (isUpdate != null) {
               saveUpdateData(title,oldTitle,oldBitmap,position)
//                if (title.isNotEmpty() && (title != oldTitle || !oldBitmap!!.sameAs(bitmap))) {
//                    if (bitmap != null && !oldBitmap!!.sameAs(bitmap)) {
//                        val res = db.insertImg(dbBitmapUtility.getBytes(bitmap!!)!!)
//                        Toast.makeText(this, "$res", Toast.LENGTH_SHORT).show()
//
//                    } else {
//                        Toast.makeText(this, "error77", Toast.LENGTH_SHORT).show()
//                    }
//                    update(position!!, CategoryModel(title, dbBitmapUtility.getBytes(bitmap!!)!!))
//
//                    //   Toast.makeText(this, "ENTER", Toast.LENGTH_SHORT).show()
//
//                } else {
//                    Toast.makeText(this, "not updated", Toast.LENGTH_SHORT).show()
//                }
               startActivity(Intent(this, Categories::class.java))
              return@setOnClickListener

            }

            if (bitmap != null && title.isNotEmpty()) {
                val img: ByteArray = dbBitmapUtility.getBytes(bitmap!!)!!
                val res = db.insertImg(img)
                //val lastId =db.getLastImgId()
                if (res) {
                    addCategory(CategoryModel(title, img))
                    startActivity(Intent(this, Categories::class.java))
                } else {
                    Toast.makeText(this, "please enter all fields", Toast.LENGTH_SHORT).show()
                }
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
//            val pickMedia= registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
//                uri -> if (uri != null)  Log.d("image","$uri")
//                else  Log.d("image","nulll")
//            }
//            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        override fun addCategory(category: CategoryModel) {
            Categories.recyclerViewAdapter.addCategory(category)
        }

        override fun update(position: Int, newCategory: CategoryModel) {
            Categories.recyclerViewAdapter.updateCategory(position, newCategory)
        }

        fun appearPreviousData(position: Int) {
            val existCategory = Categories.recyclerViewAdapter.categories[position]
            binding.categoryTitle.setText(existCategory.title)
            bitmap = dbBitmapUtility.getImage(existCategory.img)
            binding.imagePreview.setImageBitmap(bitmap)

        }

    fun saveUpdateData(
        title: String,
        oldTitle: String?,
        oldBitmap: Bitmap?,
        position: Int?
    ) {


        if(title.isNotEmpty() && bitmap != null){
            if(title!=oldTitle  || !oldBitmap!!.sameAs(bitmap)){
                db.insertImg(dbBitmapUtility.getBytes(bitmap!!)!!)
                update(position!!, CategoryModel(title, dbBitmapUtility.getBytes(bitmap!!)!!))
                Toast.makeText(this, "updated successfully", Toast.LENGTH_SHORT).show()

            }
            else{
                Toast.makeText(this, "Nothing has changed", Toast.LENGTH_SHORT).show()

            }
            startActivity(Intent(this,Categories::class.java))

        }

        else{
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()

        }

    }
    }
