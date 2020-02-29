package com.example.realestatemanager.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.example.realestatemanager.R.id
import com.example.realestatemanager.R.layout
import com.example.realestatemanager.R.string

class LogInActivity : AppCompatActivity() {
    private var registerTextView: TextView? = null
    private var logInBtn: Button? = null
    private val auth = FirebaseAuth.getInstance()
    private var emailEditText: TextInputLayout? = null
    private var passwordEditText: TextInputLayout? = null
    private var dontRememberPasswordYextView: TextView? = null
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_login)
        setViews()
        setListeners()
    }

    private fun setViews() {
        registerTextView = findViewById(id.activity_log_in_register_text_view)
        logInBtn = findViewById(id.activity_log_in_register_button)
        emailEditText = findViewById(id.activity_log_in_email_edit_text)
        passwordEditText = findViewById(id.activity_log_in_password_edit_text)
        dontRememberPasswordYextView = findViewById(id.don_t_remember_password_text_view)
    }

    private fun setListeners() {
        registerTextView!!.setOnClickListener {
            val intent = Intent(
                this@LogInActivity
                , RegisterActivity::class.java
            )
            startActivity(intent)
        }
        logInBtn!!.setOnClickListener {
            val email = emailEditText!!.editText!!.text.toString()
            val password = passwordEditText!!.editText!!.text.toString()
            logInUser(email, password)
        }
        dontRememberPasswordYextView!!.setOnClickListener {
            if (emailEditText!!.editText!!.text.toString().isEmpty()) {
                Toast.makeText(
                    this@LogInActivity, "Email field is empty"
                    , Toast.LENGTH_SHORT
                ).show()
            } else {
                displayHint()
            }
        }
    }

    private fun logInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                } else { // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this@LogInActivity
                        , "Authentication failed.", Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(this@LogInActivity, NavigationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayHint() {
        val docRef = db.collection("hints").document(
            emailEditText!!.editText!!.text.toString()
        )
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    val hint =

                            document.data?.get("hint") as String?
                    Toast.makeText(
                        this@LogInActivity, "Hint: " +
                                hint, Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@LogInActivity,
                        getString(string.email_not_found)
                        , Toast.LENGTH_SHORT
                    ).show()
                }
            } else Toast.makeText(
                this@LogInActivity,
                getString(string.no_possible_to_acess_hint)
                , Toast.LENGTH_SHORT
            ).show()
        }
    }
}