package com.wordapp.test2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FinalizeRequestsActivity : AppCompatActivity() {

    // In FinalizeRequestsActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finalize_requests)

        RequestTracker.getItems { items ->
            val adapter = FinalizeAdapter(items)
            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }


        val confirmButton: Button = findViewById(R.id.confirmButton)
        confirmButton.setOnClickListener {
            finalizeRequests()

        }
    }



    private fun finalizeRequests() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return  // Get the current user's uid

        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        val requestsRef = dbRef.child("requests")
        val finalizedRequestRef = dbRef.child("finalizedRequests").push()  // Create a new child node for the finalized request

        requestsRef.get().addOnSuccessListener { snapshot ->
            val requests = snapshot.children.mapNotNull { it.getValue(RecipientActivity.RequestItem::class.java) }
            if (requests.isNotEmpty()) {
                finalizedRequestRef.setValue(requests).addOnSuccessListener {
                    // If the copy was successful, delete the individual requests
                    requestsRef.removeValue().addOnSuccessListener {
                        Toast.makeText(this, "Request finalized successfully.", Toast.LENGTH_SHORT).show()

                        // Navigate to OngoingRequestsActivity upon successful finalization
                        val intent = Intent(this, OngoingRequestsActivity::class.java)
                        startActivity(intent)

                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed to remove old requests: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to finalize request: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No requests to finalize.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to retrieve requests: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


    inner class FinalizeAdapter(private val items: List<RecipientActivity.RequestItem>) :
        RecyclerView.Adapter<FinalizeAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemName: TextView = view.findViewById(R.id.item_name)
            val removeButton: Button = view.findViewById(R.id.removeButton)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reqfinalize, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.itemName.text = item.name
        }

        override fun getItemCount() = items.size
    }

}
