package com.example.recipe_book_app.ui.listners

import com.example.recipe_book_app.database.model.CategoryModel

interface AddCategoryListener {
    fun addCategory(category: CategoryModel)
}