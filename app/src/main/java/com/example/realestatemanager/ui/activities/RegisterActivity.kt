package com.example.realestatemanager.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.realestatemanager.R.id
import com.example.realestatemanager.R.layout
import com.example.realestatemanager.R.string
import java.util.HashMap

class RegisterActivity : AppCompatActivity() {
    private var registerBtn: Button? = null
    private var email: TextInputLayout? = null
    private var password: TextInputLayout? = null
    private var passwordHint: TextInputLayout? = null
    private var auth: FirebaseAuth? = null
    var db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_register)
        auth = FirebaseAuth.getInstance()
        setViews()
        setListeners()
    }

    private fun setViews() {
        registerBtn = findViewById(id.activity_register_register_button)
        email = findViewById(id.activity_register_email_edit_text)
        password = findViewById(id.activity_register_password_edit_text)
        passwordHint = findViewById(id.activity_register_password_hint_edit_text)
    }

    private fun setListeners() {
        registerBtn?.setOnClickListener {
            val emailString = email!!.editText!!.text.toString()
            val passwordString = password!!.editText!!.text.toString()
            val passwordHintString = passwordHint!!.editText!!.text.toString()
            if (emailString.isEmpty() || passwordString.isEmpty() ||
                passwordHintString.isEmpty()
            ) {
                displayToast(getString(string.all_fileds))
            } else {
                registerUser(emailString, passwordString)
            }
        }
    }

    private fun displayToast(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun registerUser(email: String, password: String) {
        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnFailureListener { e -> Log.d(TAG, "onFailure: $e") }
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { // Sign in success, update UI with the signed-in user's information
                    storePasswordHint()
                    displayToast(getString(string.register_sucess))
                    goToNavigationActivity()
                } else { // If sign in fails, display a message to the user.
                    displayToast(getString(string.ry_again))
                }
            }
    }

    private fun goToNavigationActivity() {
        val intent = Intent(this@RegisterActivity, NavigationActivity::class.java)
        startActivity(intent)
    }

    private fun storePasswordHint() { // Create a new user with a first and last name
        val hint: MutableMap<String, Any> =
            HashMap()
        hint["hint"] = passwordHint!!.editText!!.text.toString()
        // Add a new document with a generated ID
        db.collection("hints").document(email!!.editText!!.text.toString())
            .set(hint)
            .addOnSuccessListener { }
            .addOnFailureListener { }
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}