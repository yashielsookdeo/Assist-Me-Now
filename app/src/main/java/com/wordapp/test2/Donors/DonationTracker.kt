package com.wordapp.test2.Donors

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object DonationTracker {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    fun addItem(item: String, quantity: Int) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            val donationRef = database.getReference("users/$uid/donations")
            donationRef.child(item).setValue(quantity)
        } else {
            // Handle the case where the user is not logged in
        }
    }

    fun getItems(callback: (Map<String, Long>) -> Unit) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            val donationRef = database.getReference("users/$uid/donations")
            donationRef.get().addOnSuccessListener {
                val items = it.value as Map<String, Long>? ?: emptyMap()
                callback(items)
            }
        } else {
            // Handle the case where the user is not logged in
        }
    }

    fun removeItem(item: String) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            val donationRef = database.getReference("users/$uid/donations")
            donationRef.child(item).removeValue()
        } else {
            // Handle the case where the user is not logged in
        }
    }
}
