package com.example.recipe_book_app.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recipe_book_app.auth.UserController
import com.example.recipe_book_app.databinding.ActivityCreateAnAccountBinding
import com.example.recipe_book_app.validation.Validation
import com.example.recipe_book_app.database.model.User


class CreateAnAccount : AppCompatActivity() {
    //define variables
    private lateinit var binding: ActivityCreateAnAccountBinding
    private lateinit var  validation: Validation
    private lateinit var  controller: UserController
companion object{
    var emailStr: String? = null
    var nameStr: String? =null
    var passwordStr: String? =null
    var confirmPass: String? = null

}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAnAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //when click on create an account
        binding.buttonSignUp.setOnClickListener {
            emailStr = binding.editTextTextEmailAddressSignUp.text.toString()
            nameStr = binding.editTextNameSignUp.text.toString()
            passwordStr = binding.editTextPassSignUp.text.toString()
            confirmPass = binding.editTextConfirmPasswordSignUp.text.toString()

            validation = Validation()

            //check that the user fill all fields
            controller = UserController(this@CreateAnAccount)
            val user = User(emailStr!!, nameStr!!, passwordStr!!)
            if (validation.checkAllFields(user) && confirmPass(confirmPass!!, passwordStr!!)) {
                if (controller.signUp(user)) {
                    val intent = Intent(this@CreateAnAccount, SignIn::class.java)
                    startActivity(intent)
                } else {
                    binding.editTextTextEmailAddressSignUp.error = "This email already exists!"
                }
            } else {
                validationMessage(nameStr!!, emailStr!!, passwordStr!!)
            }
        }

        // hide pass
      binding.signIn.setOnClickListener {
          val intent = Intent(this, SignIn::class.java)
          startActivity(intent)
      }

    }

    private fun validationMessage(
        nameStr: String,
        emailStr: String,
        passwordStr: String,
    ) {

        checkName(nameStr)
        checkEmail(emailStr)
        checkPassword(passwordStr)
        confirmPassMessage()
    }

    private fun checkName(nameStr: String) {
        if (nameStr.isEmpty()) {
            binding.editTextNameSignUp.error = "This field is required"
        } else if (!validation.validName(nameStr)) {
            binding.editTextNameSignUp.error = "Invalid name"
        }
    }

    private fun checkEmail(emailStr: String) {
        if (emailStr.isEmpty()) {
            binding.editTextTextEmailAddressSignUp.error = "This field is required"
        } else if (!validation.validEmail(emailStr)) {
            binding.editTextTextEmailAddressSignUp.error = "Invalid email"
        }
    }



    private fun checkPassword(passwordStr: String) {
        if (passwordStr.isEmpty()) {
            binding.editTextPassSignUp.error = "This field is required"
        } else if (!validation.validPassword(passwordStr)) {
            binding.editTextPassSignUp.error=("Invalid password")
        }
    }

    private fun confirmPass(confirmPass:String, passwordStr: String) = confirmPass==passwordStr

    private fun confirmPassMessage(){
        if(confirmPass?.isEmpty() == true){
            binding.editTextConfirmPasswordSignUp.error = "This field is required"

        }
        else if(!confirmPass(confirmPass!!, passwordStr!!)){
            binding.editTextConfirmPasswordSignUp.error = "incorrect password"
        }

    }




}



