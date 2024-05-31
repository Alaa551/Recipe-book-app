package com.example.recipe_book_app.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.recipe_book_app.database.Database
import com.example.recipe_book_app.database.model.User
import com.example.recipe_book_app.ui.activities.SignIn

class UserController(var context: Context) {
    private val db: Database = Database(context)


    fun signUp(user: User) = if (isEmailExist(user.email)) {
        false
    } else {
        db.insertUser(user)
    }


    private fun isEmailExist(email: String): Boolean {
        val users: ArrayList<User> = db.getAllUsers()

        for (u in users) {
            if (u.email == email) {
                return true
            }
        }

        return false

    }

    fun signIn(email: String, pass: String): Int {
        return db.getUser(email, pass)

    }

    fun logout() {
        val intent = Intent(this.context, SignIn::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }


}



