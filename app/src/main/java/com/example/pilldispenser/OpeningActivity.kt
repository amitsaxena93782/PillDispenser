package com.example.pilldispenser

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class OpeningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opening)

        val titleTextView: TextView = findViewById(R.id.titleTextView)
        val ipAddressEditText: EditText = findViewById(R.id.ipAddressEditText)
        val submitButton: Button = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val ipAddress = ipAddressEditText.text.toString().trim()
            if (ipAddress.isNotEmpty()) {
                // Save the IP address and pass it to the Retrofit instance
                RetrofitInstance.setBaseUrl("http://$ipAddress/")

                // Navigate to the MainActivity
                val intent = Intent(this@OpeningActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please enter a valid IP address", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
