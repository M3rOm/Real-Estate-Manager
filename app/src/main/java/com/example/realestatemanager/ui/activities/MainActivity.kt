package com.example.realestatemanager.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.realestatemanager.R.id
import com.example.realestatemanager.R.layout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        /** BUG 1) Line number 17
        Original: this.textViewMain = findViewById(R.id.activity_second_activity_text_view_main);
        Correct: this.textViewMain = findViewById(R.id.activity_main_activity_text_view_main);
        Reason: We used activity_main layout, so we should find views within that layout, not activity_second
         **/

        init()
    }

    private fun init() {
        findViewById<View>(id.activity_main_login_button).setOnClickListener {
            val intent = Intent(
                    this@MainActivity
                    , LogInActivity::class.java
            )
            startActivity(intent)
        }
        findViewById<View>(id.activity_main_signup_button).setOnClickListener {
            val intent = Intent(
                    this@MainActivity
                    , RegisterActivity::class.java
            )
            startActivity(intent)
        }
    }
    /** BUG 2) Line number 32
    Original: this.textViewQuantity.setText(quantity);
    Correct: this.textViewQuantity.setText(Integer.toString(quantity));
    Reason: TextViews are for showing String, not Integer. We need to transform our string to an Integer.
     **/
}