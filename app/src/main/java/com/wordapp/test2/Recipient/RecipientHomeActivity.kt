package com.wordapp.test2.Recipient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wordapp.test2.AboutActivity
import com.wordapp.test2.Admin.AdminCompletedDon
import com.wordapp.test2.Admin.AdminCompletedReq
import com.wordapp.test2.Admin.AdminOngoingDon
import com.wordapp.test2.Admin.AdminOngoingReq
import com.wordapp.test2.OngoingRequestsActivity
import com.wordapp.test2.R
import com.wordapp.test2.RecipientActivity

class RecipientHomeActivity : AppCompatActivity() {
    // In RecipientHomeActivity.kt

    private lateinit var messageTextView: TextView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient_home)

        messageTextView = findViewById(R.id.messageTextView)
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        fetchMessage()
    }

    private fun fetchMessage() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser?.uid ?: return)
            .child("messages")


        databaseReference.child("message").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val message = snapshot.getValue(String::class.java)
                messageTextView.text = message

                val expiryTimestamp = snapshot.child("expiryTimestamp").getValue(Long::class.java)
                if (expiryTimestamp != null && System.currentTimeMillis() > expiryTimestamp) {
                    // Delete the expired message
                    databaseReference.child("message").setValue(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }


    fun openOngoingRequestsActivity(view: View) {
        val intent = Intent(this, OngoingRequestsActivity::class.java)
        startActivity(intent)
    }

    fun openAboutActivity(view: View) {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }


    fun openRecipientActivity(view: View) {
        val intent = Intent(this, RecipientActivity::class.java)
        startActivity(intent)
    }
}
