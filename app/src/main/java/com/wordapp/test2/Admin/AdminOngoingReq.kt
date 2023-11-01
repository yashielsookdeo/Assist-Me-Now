package com.wordapp.test2.Admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.wordapp.test2.OngoingRequestsActivity
import com.wordapp.test2.R

class AdminOngoingReq : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var requestsAdapter: RequestsAdapter
    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            updateRequestsFromSnapshot(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle database error
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_ongoing_req)

        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.addValueEventListener(valueEventListener)

        setupRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseReference.removeEventListener(valueEventListener)
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        requestsAdapter = RequestsAdapter(mutableListOf())
        recyclerView.adapter = requestsAdapter
    }

    private fun updateRequestsFromSnapshot(dataSnapshot: DataSnapshot) {
        val requestsList = mutableListOf<FinalizedRequest>()
        for (userSnapshot in dataSnapshot.children) {
            val userId = userSnapshot.key ?: ""
            for (requestSnapshot in userSnapshot.child("finalizedRequests").children) {
                val itemsList = mutableListOf<RequestItem>()
                for (itemSnapshot in requestSnapshot.children) {
                    val itemName = itemSnapshot.child("name").getValue(String::class.java) ?: ""
                    val itemRequested = itemSnapshot.child("requested").getValue(Boolean::class.java) ?: false
                    itemsList.add(RequestItem(itemName, if(itemRequested) 1 else 0))
                }
                val requestId = requestSnapshot.key ?: ""
                requestsList.add(FinalizedRequest(requestId, itemsList, userId))
            }
        }
        requestsAdapter.updateRequests(requestsList)
    }


    private fun fetchRequests() {
        Log.d("AdminOngoingReq", "fetchRequests called")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                updateRequestsFromSnapshot(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    // ... (the rest of your existing methods and classes, updated accordingly)

    data class RequestItem(
        val name: String = "",
        val quantity: Long = 0
    )

    data class FinalizedRequest(
        val id: String = "",
        val items: List<RequestItem> = listOf(),
        val userId: String = ""
    )

    inner class RequestsAdapter(private val requests: MutableList<FinalizedRequest>) :
        RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adminongoingreqview, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val request = requests[position]
            val itemsString = request.items.joinToString { "${it.name}: ${it.quantity}" }
            holder.itemName.text = "User ID: ${request.userId}\nItems: $itemsString"

            holder.acceptButton.setOnClickListener {
                moveRequestToCompleted(request)
                removeFinalizedRequest(request)
                sendMessage(request.userId, "Your request has been accepted")
            }

            holder.rejectButton.setOnClickListener {
                removeFinalizedRequest(request)
                sendMessage(request.userId, "Your request has been rejected")
            }
        }
        fun sendMessage(recipientUserId: String, message: String) {
            val timestamp = System.currentTimeMillis()
            val messageData = mapOf(
                "message" to message,
                "timestamp" to timestamp
            )
            FirebaseDatabase.getInstance().getReference("users/$recipientUserId/messages").setValue(messageData)
        }

        fun updateRequests(newRequests: List<FinalizedRequest>) {
            requests.clear()
            requests.addAll(newRequests)
            notifyDataSetChanged()
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemName: TextView = view.findViewById(R.id.item_name)
            val acceptButton: Button = view.findViewById(R.id.accept_button)
            val rejectButton: Button = view.findViewById(R.id.reject_button)  // assuming you have this button in your layout
        }

        override fun getItemCount() = requests.size
    }
    private fun moveRequestToCompleted(request: FinalizedRequest) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        val completedRequestsRef = dbRef.child(request.userId).child("CompletedRequests").child(request.id)
        completedRequestsRef.setValue(request).addOnFailureListener {
            Log.e("AdminOngoingReq", "Failed to move request to CompletedRequests: ${it.message}")
        }
    }

    private fun removeFinalizedRequest(request: FinalizedRequest) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        val finalizedRequestRef = dbRef.child(request.userId).child("finalizedRequests").child(request.id)
        finalizedRequestRef.removeValue().addOnFailureListener {
            Log.e("AdminOngoingReq", "Failed to remove finalizedRequest: ${it.message}")
        }

}
}

