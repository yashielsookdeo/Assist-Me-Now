package com.wordapp.test2

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object RequestTracker {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val requestRef = database.getReference("users/${auth.currentUser?.uid}/requests")

    fun addItems(items: List<RecipientActivity.RequestItem>) {
        items.forEach { item ->
            requestRef.push().setValue(item)
        }
    }

    fun getItems(callback: (List<RecipientActivity.RequestItem>) -> Unit) {
        requestRef.get().addOnSuccessListener {
            val items = it.children.mapNotNull { snapshot ->
                snapshot.getValue(RecipientActivity.RequestItem::class.java)
            }
            callback(items)
        }

    }

    fun finalizeRequests() {
        val finalizedRef = database.getReference("users/${auth.currentUser?.uid}/finalizedRequests")
        getItems { items ->
            if (items.isNotEmpty()) {
                finalizedRef.setValue(items).addOnSuccessListener {
                    // Clear the current requests after successfully moving them to finalizedRequests
                    requestRef.removeValue().addOnSuccessListener {
                        // Optionally: Notify the user
                    }.addOnFailureListener {
                        // Handle failure to clear current requests
                    }
                }.addOnFailureListener {
                    // Handle failure to finalize requests
                }
            } else {
                // Optionally: Notify the user there are no items to finalize
            }
        }
    }

}
