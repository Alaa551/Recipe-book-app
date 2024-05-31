package com.example.recipe_book_app.ui.listners

import com.example.recipe_book_app.database.model.CategoryModel

interface UpdateListner {
    fun update(position: Int,newCategory: CategoryModel)
}