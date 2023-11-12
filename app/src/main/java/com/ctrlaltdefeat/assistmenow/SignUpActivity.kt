package com.ctrlaltdefeat.assistmenow

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import com.ctrlaltdefeat.assistmenow.database.Firebase
import com.ctrlaltdefeat.assistmenow.donors.DonorDashboardActivity
import com.ctrlaltdefeat.assistmenow.recipients.RecipientDashboardActivity

class SignUpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var buttonBack: ImageView
    private lateinit var buttonSignUp: Button
    private lateinit var spinner: Spinner
    private lateinit var spinnerRoleSelected: String

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)
        buttonSignUp = findViewById(R.id.b_signup)

        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }

        buttonSignUp.setOnClickListener {
            val email = findViewById<EditText>(R.id.input_email).text.toString()
            val password = findViewById<EditText>(R.id.input_password).text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Invalid Email - Please try again", Toast.LENGTH_LONG).show()
            } else {
                if (password.isEmpty()) {
                    Toast.makeText(this, "Invalid Password - Please try again", Toast.LENGTH_LONG).show()
                } else {
                    if (spinnerRoleSelected.isEmpty()) {
                        Toast.makeText(this, "Invalid Role Selected - Please try again", Toast.LENGTH_LONG).show()
                    } else {
                        Log.d("cock", "The spinner role: " + spinnerRoleSelected)
                        Firebase.userSignUp(email, password, spinnerRoleSelected) { success, message ->
                            if (success) {
                                Firebase.getUserRole(false) { roleSuccess, role ->
                                    if (roleSuccess) {
                                        Log.d("cock", "What the fuck?????? " + role.lowercase())
                                        when (role.lowercase()) {
                                            "donor" -> {
                                                val intent = Intent(this, DonorDashboardActivity::class.java)

                                                startActivity(intent)
                                            }
                                            "recipient" -> {
                                                val intent = Intent(this, RecipientDashboardActivity::class.java)

                                                startActivity(intent)
                                            }
                                            else -> {
                                                Toast.makeText(this, "Unknown role - Please login again", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(this, "Couldn't get your role - Please login again", Toast.LENGTH_LONG).show()
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
    }

    @SuppressLint("ResourceType")
    private fun initSpinner() {
        spinner = findViewById(R.id.spinner_role)

        val adapter = ArrayAdapter.createFromResource(this, R.array.spinner_role_items, R.layout.spinner_layout)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)

        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initButtons()
        initSpinner()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        spinnerRoleSelected = parent?.selectedItem.toString()

        //Toast.makeText(this, parent?.selectedItem.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}