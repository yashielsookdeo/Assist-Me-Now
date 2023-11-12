package com.ctrlaltdefeat.assistmenow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.ctrlaltdefeat.assistmenow.admin.AdminDashboardActivity
import com.ctrlaltdefeat.assistmenow.database.Firebase
import com.ctrlaltdefeat.assistmenow.donors.DonorDashboardActivity
import com.ctrlaltdefeat.assistmenow.recipients.RecipientDashboardActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var buttonBack: ImageView
    private lateinit var buttonLogin: Button

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)
        buttonLogin = findViewById(R.id.b_login)

        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }

        buttonLogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.input_email).text.toString()
            val password = findViewById<EditText>(R.id.input_password).text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Invalid Email - Please try again", Toast.LENGTH_LONG).show()
            } else {
                if (password.isEmpty()) {
                    Toast.makeText(this, "Invalid Password - Please try again", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Firebase.userLogin(email, password) { success, message, role ->
                        if (success) {
                            when (role.lowercase()) {
                                "donor" -> {
                                    val intent = Intent(this, DonorDashboardActivity::class.java)

                                    startActivity(intent)
                                }
                                "recipient" -> {
                                    val intent = Intent(this, RecipientDashboardActivity::class.java)

                                    startActivity(intent)
                                }
                                "admin" -> {
                                    val intent = Intent(this, AdminDashboardActivity::class.java)

                                    startActivity(intent)
                                }
                                else -> {
                                    Toast.makeText(this, "Unknown role - Please login again", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "${message}. Please try again", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initButtons()
    }
}