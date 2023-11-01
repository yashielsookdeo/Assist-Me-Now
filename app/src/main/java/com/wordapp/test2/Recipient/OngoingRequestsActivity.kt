package com.wordapp.test2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wordapp.test2.Donors.DonorHomeActivity
import com.wordapp.test2.Recipient.RecipientHomeActivity

class OngoingRequestsActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_going_requests)

        databaseReference = FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser?.uid ?: return)
            .child("finalizedRequests")

        setupRecyclerView()  // Call this method to set up the RecyclerView
    }
    fun openRecipientHomeActivity(view: View) {
        val intent = Intent(this, RecipientHomeActivity::class.java)
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val requestsList = mutableListOf<Request>()
                for (requestSnapshot in dataSnapshot.children) {
                    val itemsList = mutableListOf<RecipientActivity.RequestItem>()
                    for (itemSnapshot in requestSnapshot.children) {
                        val item = itemSnapshot.getValue(RecipientActivity.RequestItem::class.java)
                        if (item != null) {
                            itemsList.add(item)
                        }
                    }
                    val request = Request(id = requestSnapshot.key ?: "", items = itemsList)
                    requestsList.add(request)
                }
                recyclerView.adapter = RequestsAdapter(requestsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    data class Request(
        val id: String = "",
        val items: List<RecipientActivity.RequestItem> = listOf()
    )

    inner class RequestsAdapter(private val requests: List<Request>) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemName: TextView = view.findViewById(R.id.item_name)  // Assuming each item has a name to be displayed
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.ongoingreqview, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val request = requests[position]
            // Assuming each item has a name to be displayed
            holder.itemName.text = request.items.joinToString(", ") { it.name }
        }

        override fun getItemCount() = requests.size
    }
}
