package com.example.recipe_book_app.validation

import android.util.Patterns
import com.example.recipe_book_app.database.model.User
import java.util.regex.Pattern

class Validation {

        fun validName(name: String): Boolean {
            val pattern = "^[A-Za-z]+$"
            return !(name=="" || !Pattern.matches(pattern, name))
        }

        fun validEmail(email: String): Boolean {
            return !(email=="" || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        }

        fun validPassword(pass: String): Boolean {
            val pattern = "^(?=.*\\d)" +
                    "(?=.[a-z]).{8,20}$"
            return !(pass=="" || !Pattern.matches(pattern, pass))
        }


    fun checkAllFields(user: User): Boolean {
            return !(!validName(user.name) || !validEmail(user.email)
                    || !validPassword(user.password) )
        }


    }
