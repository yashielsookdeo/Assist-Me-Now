package com.wordapp.test2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wordapp.test2.Admin.AdminHomeActivity
import com.wordapp.test2.Donors.DonorActivity
import com.wordapp.test2.Donors.DonorHomeActivity
import com.wordapp.test2.Recipient.RecipientHomeActivity
import com.wordapp.test2.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        user?.let {
                            val uid = it.uid
                            databaseReference.child("users").child(uid).child("role")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val userRole = dataSnapshot.getValue(String::class.java)

                                        if (userRole != null) {
                                            // Now you have the user's role in the 'userRole' variable
                                            // Check the user's role and start the appropriate activity
                                            when (userRole) {
                                                "Donor" -> {
                                                    val intent = Intent(this@SignInActivity, DonorHomeActivity::class.java)
                                                    startActivity(intent)
                                                }
                                                "Recipient" -> {
                                                    val intent = Intent(this@SignInActivity, RecipientHomeActivity::class.java)
                                                    startActivity(intent)
                                                }
                                                "Admin" -> {
                                                    val intent = Intent(this@SignInActivity, AdminHomeActivity::class.java)
                                                    startActivity(intent)
                                                }
                                                else -> {
                                                    // Handle other roles or unknown roles
                                                    Toast.makeText(
                                                        this@SignInActivity,
                                                        "Unknown user role: $userRole",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    Toast.makeText(
                                                        this@SignInActivity,
                                                        "Unknown user role: $userRole",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        } else {
                                            // Handle the case where 'userRole' is null
                                            Toast.makeText(
                                                this@SignInActivity,
                                                "User role not found in the database",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Toast.makeText(
                                            this@SignInActivity,
                                            "Failed to get user role: ${databaseError.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            task.exception.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    this@SignInActivity,
                    "Empty Fields Are not Allowed !!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
