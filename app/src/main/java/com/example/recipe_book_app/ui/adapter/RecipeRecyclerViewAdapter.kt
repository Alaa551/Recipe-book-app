package com.example.recipe_book_app.ui.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe_book_app.ui.activities.AddNewRecipe
import com.example.recipe_book_app.R
import com.example.recipe_book_app.ui.listners.RecyclerOnItemClick
import com.example.recipe_book_app.database.Database
import com.example.recipe_book_app.database.DbBitmapUtility
import com.example.recipe_book_app.database.model.RecipeModel
import com.example.recipe_book_app.ui.listners.DeleteListner
import com.google.android.material.card.MaterialCardView


class RecipeRecyclerViewAdapter(var recipes: MutableList<RecipeModel>, var context: Context, var recyclerOnItemClick: RecyclerOnItemClick, val sharedPreferences: SharedPreferences, val deleteListner: DeleteListner) :
    RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder>() {

    private val db = Database(context)
   // val userType:Int= getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE).getInt("user_type",0)
   private val dbBitmapUtility: DbBitmapUtility = DbBitmapUtility()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeViewHolder {
        val v: View =
          LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, null, false)

        return RecipeViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: RecipeViewHolder,
        position: Int
    ) {
        val c: RecipeModel = recipes[position]
        holder.recipeTitle.text = c.title
        holder.recipeTime.text = "${c.time} Mins "
        holder.recipeRate.text = (c.rate).toString()
        holder.recipeImg.setImageBitmap(dbBitmapUtility.getImage(c.img))

        holder.updateIcon.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, AddNewRecipe::class.java)
            intent.putExtra("update","update")
            intent.putExtra("position",position)
            context.startActivity(intent)
        })

        holder.deleteIcon.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure to delete this Recipe ?")
            builder.setPositiveButton("Sure") { dialog, _ ->
                deleteListner.delete(position)
                Toast.makeText(context, "Recipe deleted successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        })


    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    // holder class for recycler view
    public inner class RecipeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var recipeTitle: TextView = itemView.findViewById(R.id.recipe_title)
        var recipeImg: ImageView = itemView.findViewById(R.id.recipe_img)
        var recipeTime: TextView = itemView.findViewById(R.id.recipe_time)
        var recipeRate: TextView = itemView.findViewById(R.id.recipe_rate)
        var cardView: MaterialCardView = itemView.findViewById(R.id.recipe_cardView)
        var updateIcon: ImageView = cardView.findViewById<ImageView>(R.id.update_img)
        var deleteIcon: ImageView = cardView.findViewById<ImageView>(R.id.delete_img)


        // onclick on recipe
     init {
         cardView.setOnClickListener {
             recyclerOnItemClick.onItemClick(adapterPosition)

         }
            permissionsForAdmin(sharedPreferences.getInt("user_type",-1),itemView)
     }


    }

    fun filterList(filterList: MutableList<RecipeModel>){
        recipes=filterList
        notifyDataSetChanged()

    }


    fun permissionsForAdmin(userType:Int,itemView:View) {
        val layout = itemView.findViewById<LinearLayout>(R.id.update_and_delete_layout)
        val card = itemView.findViewById<MaterialCardView>(R.id.recipe_cardView)
        val time = card.findViewById<TextView>(R.id.recipe_time)
        val layoutParams= time.layoutParams as FrameLayout.LayoutParams
        if (userType == 0) {
            layout.visibility=View.GONE
            layoutParams.gravity=Gravity.BOTTOM or Gravity.START
            layoutParams.marginStart= 50
            layoutParams.bottomMargin=100

            time.layoutParams= layoutParams


        }
        else if(userType==1){
            layout.visibility=View.VISIBLE
        }

    }

    fun addRecipe(recipe: RecipeModel, categoryId:Int) {
        db.insertRecipe(recipe,categoryId)
        recipe.id = db.getLastRecipeId()

        recipes.add(recipe)
        notifyItemInserted(recipes.size - 1)
    }

    fun updateRecipe(position: Int,newRecipe: RecipeModel) {
        val item= recipes[position]
        if (db.updateRecipe(item.id,newRecipe)) {
            recipes[position] = newRecipe
            notifyItemChanged(position)
        }
    }

    fun deleteRecipe(position: Int) {
        var item = recipes[position]
        db.deleteRecipe(item.id)
        recipes.removeAt(position)
        notifyItemRemoved(position)
    }





}





