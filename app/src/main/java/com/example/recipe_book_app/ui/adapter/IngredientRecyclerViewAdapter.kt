package com.example.recipe_book_app.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe_book_app.R
import com.example.recipe_book_app.database.Database
import com.example.recipe_book_app.database.model.IngredientModel

class IngredientRecyclerViewAdapter (var ingredients: MutableList<IngredientModel>, var context: Context) :
    RecyclerView.Adapter<IngredientRecyclerViewAdapter.TaskViewHolder>() {
    private val db = Database(context)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, null, false)

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

        val c: IngredientModel = ingredients[position]
        holder.ingredientName.text = c.name
        holder.ingredientAmount.text = "${c.amount} g "




    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    // holder class for recycler view
    public inner class TaskViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var ingredientName: TextView = itemView.findViewById(R.id.ingredient_name)
        var ingredientAmount: TextView = itemView.findViewById(R.id.ingredient_amount)


    }

    fun addIngredient(ingredient: IngredientModel, recipeId:Int) {
        if(db.addIngredient(ingredient,recipeId)) {
            ingredient.id = db.getLastIngredientId()

            ingredients.add(ingredient)
            notifyItemInserted(ingredients.size - 1)
        }
        else{
            Log.d("ingredient add","error in add")
        }

    }


}



