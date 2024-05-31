package com.example.recipe_book_app.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.recipe_book_app.R
import com.example.recipe_book_app.auth.UserController
import com.example.recipe_book_app.databinding.SignInBinding
import com.example.recipe_book_app.database.Database


class SignIn : AppCompatActivity() {
    //define variables
    private lateinit var binding: SignInBinding
    private lateinit var controller: UserController
    private val db: Database = Database(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("mySharedPref", Context.MODE_PRIVATE)


        // when click on sign in button
        binding.button.setOnClickListener {
            val emailStr: String = binding.editTextTextEmailAddress.text.toString()
            val passwordStr: String = binding.editTextTextPassword.text.toString()
            //check that the user fill all fields
            if (emailStr.isEmpty() || passwordStr.isEmpty()) {
                val builder = AlertDialog.Builder(this@SignIn)
                builder.setMessage("Please enter all fields")
                builder.setPositiveButton("OK", null)
                builder.show()
            } else {
                controller = UserController(this@SignIn)
                val res = controller.signIn(emailStr, passwordStr)
                if (res != -1) {

                    Toast.makeText(this, "welcome back", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomePage::class.java)
                    val userType = db.getUserType(res)
                    val editor = sharedPreferences.edit()
                    editor.putInt("user_type", userType)
                    editor.apply()
                    startActivity(intent)
                } else {
                    val builder = AlertDialog.Builder(this@SignIn)
                    builder.setMessage("incorrect email or password ,please try again")
                    builder.setPositiveButton("OK", null)
                    builder.show()
                }
            }
        }

        binding.imageViewEyeSignIn.setOnClickListener {
            if (binding.editTextTextPassword.transformationMethod == HideReturnsTransformationMethod.getInstance()) {
                binding.imageViewEyeSignIn.setImageResource(R.drawable.eye)
                binding.editTextTextPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            } else {
                binding.imageViewEyeSignIn.setImageResource(R.drawable.hide_eye)
                binding.editTextTextPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            }
        }

        binding.createAcoount.setOnClickListener {
            val intent = Intent(this, CreateAnAccount::class.java)
            startActivity(intent)
        }


    }


}


