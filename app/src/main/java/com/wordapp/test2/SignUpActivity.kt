// SignUpActivity.kt

package com.wordapp.test2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.wordapp.test2.Donors.DonorActivity
import com.wordapp.test2.RecipientActivity
import com.wordapp.test2.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference

        // Initialize the Spinner with role options
        val rolesSpinner = binding.rolesSpinner
        val rolesAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.roles_array,
            android.R.layout.simple_spinner_item
        )
        rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rolesSpinner.adapter = rolesAdapter

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.rolesSpinner.setBackground(getResources().getDrawable(R.drawable.spinner_backround))

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val selectedRole = binding.rolesSpinner.selectedItem.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                user?.let {
                                    // Save the selected role to Firebase Realtime Database
                                    val uid = it.uid
                                    databaseReference.child("users").child(uid).child("role")
                                        .setValue(selectedRole)

                                    // Check the user's role and start the appropriate activity
                                    when (selectedRole.toLowerCase()) {
                                        "donor" -> {
                                            val intent = Intent(this, DonorActivity::class.java)
                                            startActivity(intent)
                                        }
                                        "recipient" -> {
                                            val intent = Intent(this, RecipientActivity::class.java)
                                            startActivity(intent)
                                        }
                                        else -> {
                                            val error = task.exception?.message
                                            // Handle other roles or unknown roles
                                            Toast.makeText(
                                                this,
                                                "Unknown user role: $selectedRole",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}

