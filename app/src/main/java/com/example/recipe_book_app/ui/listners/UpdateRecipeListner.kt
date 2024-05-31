package com.example.recipe_book_app.ui.listners

import com.example.recipe_book_app.database.model.RecipeModel

interface UpdateRecipeListner {
    fun update(position: Int,newRecipe: RecipeModel)

}