package com.example.recipe_book_app.ui.listners

import com.example.recipe_book_app.database.model.IngredientModel

interface AddIngredientListner {
    fun addIngredient(ingredientModel: IngredientModel, recipeId: Int)
}