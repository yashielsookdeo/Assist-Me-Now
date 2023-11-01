package com.wordapp.test2.Admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.wordapp.test2.R

class AdminCompletedReq : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: CompletedRequestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_completed_req)

        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        setupRecyclerView()
        fetchCompletedRequests()
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CompletedRequestsAdapter(mutableListOf())
        recyclerView.adapter = adapter
    }

    private fun fetchCompletedRequests() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val completedRequestsList = mutableListOf<CompletedRequest>()
                for (userSnapshot in dataSnapshot.children) {
                    val uid = userSnapshot.key ?: ""
                    for (requestSnapshot in userSnapshot.child("CompletedRequests").children) {
                        // Assuming request data is a map
                        val requestData = requestSnapshot.value as? Map<String, Any> ?: continue
                        completedRequestsList.add(CompletedRequest(uid, requestData))
                    }
                }
                adapter.updateRequests(completedRequestsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    data class CompletedRequest(val uid: String, val requestData: Map<String, Any>)

    class CompletedRequestsAdapter(private val requests: MutableList<CompletedRequest>) :
        RecyclerView.Adapter<CompletedRequestsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_completed_request, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val request = requests[position]
            holder.userId.text = request.uid
            holder.requestData.text = request.requestData.toString()
        }

        override fun getItemCount() = requests.size

        fun updateRequests(newRequests: List<CompletedRequest>) {
            requests.clear()
            requests.addAll(newRequests)
            notifyDataSetChanged()
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val userId: TextView = view.findViewById(R.id.userId)
            val requestData: TextView = view.findViewById(R.id.requestData)
        }
    }
}
