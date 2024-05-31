package com.example.recipe_book_app.ui.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe_book_app.ui.activities.AddNewCategory
import com.example.recipe_book_app.R
import com.example.recipe_book_app.ui.listners.RecyclerOnItemClick
import com.example.recipe_book_app.database.Database
import com.example.recipe_book_app.database.DbBitmapUtility
import com.example.recipe_book_app.database.model.CategoryModel
import com.example.recipe_book_app.ui.listners.DeleteListner
import com.google.android.material.card.MaterialCardView


class RecyclerViewAdapter(var categories: MutableList<CategoryModel>, var context: Context, var recyclerOnItemClick: RecyclerOnItemClick, val sharedPreferences: SharedPreferences, val deleteListner: DeleteListner) :
     RecyclerView.Adapter<RecyclerViewAdapter.TaskViewHolder>() {

     private val db = Database(context)
    private  val dbBitmapUtility: DbBitmapUtility = DbBitmapUtility()



    override fun onCreateViewHolder(
         parent: ViewGroup,
         viewType: Int
     ): TaskViewHolder {
         val v: View =
             LayoutInflater.from(parent.context).inflate(R.layout.category_item, null, false)

         return TaskViewHolder(v)
     }

     override fun onBindViewHolder(
         holder: TaskViewHolder,
         position: Int
     ) {

         //animation
          val animation= AnimationUtils.loadAnimation(holder.itemView.context,
              android.R.anim.slide_in_left)
         holder.itemView.startAnimation(animation)

         val c: CategoryModel = categories[position]
         holder.catTitle.text = c.title
         holder.catImg.setImageBitmap(dbBitmapUtility.getImage(c.img))

         // delete operation
         holder.deleteIcon.setOnClickListener(View.OnClickListener {
             val builder = AlertDialog.Builder(context)
             builder.setMessage("Are you sure to delete this category ?")
             builder.setPositiveButton("Sure") { dialog, _ ->
                 deleteListner.delete(position)
                 Toast.makeText(context, "Category deleted successfully", Toast.LENGTH_SHORT).show()
                 dialog.dismiss()

             }
             builder.setNegativeButton("Cancel") { dialog, _ ->
                 dialog.dismiss()
             }
             builder.show()
         })

         holder.updateIcon.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, AddNewCategory::class.java)
             intent.putExtra("update","update")
             intent.putExtra("position",position)
             context.startActivity(intent)
         })


     }

     override fun getItemCount(): Int {
         return categories.size
     }

     // holder class for recycler view
     public inner class TaskViewHolder(itemView: View) :
         RecyclerView.ViewHolder(itemView) {
         var catTitle: TextView = itemView.findViewById(R.id.category_title)
         var catImg: ImageView = itemView.findViewById(R.id.category_img)
         var cardView: MaterialCardView = itemView.findViewById(R.id.category_cardView)
         var deleteIcon: ImageView = cardView.findViewById<ImageView>(R.id.delete_img)
         var updateIcon: ImageView = cardView.findViewById<ImageView>(R.id.update_img)
         init {
             cardView.setOnClickListener {
                 recyclerOnItemClick.onItemClick(adapterPosition)

             }
             permissionsForAdmin(sharedPreferences.getInt("user_type",-1),itemView)

         }


     }

    fun filterList(filterList: MutableList<CategoryModel>){
        categories=filterList
        notifyDataSetChanged()

    }

    fun permissionsForAdmin(userType:Int,itemView:View) {
        val layout = itemView.findViewById<LinearLayout>(R.id.update_and_delete_layout)

        if (userType == 0) {
            layout.visibility=View.GONE
        }
        else if(userType==1){
            layout.visibility=View.VISIBLE
        }
    }

    fun addCategory(category: CategoryModel) {
        db.insertCategory(category)
        category.id = db.getLastCategoryId()

        categories.add(category)
        notifyItemInserted(categories.size - 1)
    }



     fun deleteCategory(position: Int) {
         var item = categories[position]
         if (db.deleteCategory(item.id)) {
             categories.removeAt(position)
             notifyItemRemoved(position)
         } else {
            Log.d("from delete category",item.id.toString())
         }
     }

     fun updateCategory(position: Int,newCategory: CategoryModel) {
         var item = categories[position]
         if (db.updateCategory(item.id,newCategory)) {
             categories[position] = newCategory
notifyDataSetChanged()
             Log.d("adapter","done")
         }



     }

 }





