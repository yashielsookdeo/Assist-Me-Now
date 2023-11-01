package com.wordapp.test2.Donors

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
import com.wordapp.test2.R

class OngoingDonationsActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ongoing_donations)

        databaseReference = FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser?.uid ?: return)
            .child("finalizedDonations")

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val donationsList = mutableListOf<Donation>()
                for (donationSnapshot in dataSnapshot.children) {
                    val itemsMap = mutableMapOf<String, Long>()
                    for (itemSnapshot in donationSnapshot.children) {
                        val itemName = itemSnapshot.key ?: continue
                        val itemQuantity = itemSnapshot.value as? Long ?: continue
                        itemsMap[itemName] = itemQuantity
                    }
                    val donation = Donation(id = donationSnapshot.key ?: "", items = itemsMap)
                    donationsList.add(donation)
                }
                recyclerView.adapter = DonationsAdapter(donationsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    data class Donation(
        val id: String = "",
        val items: Map<String, Long> = mapOf()
    )

    inner class DonationsAdapter(private val donations: List<Donation>) : RecyclerView.Adapter<DonationsAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemName: TextView = view.findViewById(R.id.item_name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.ongoingdonview, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val donation = donations[position]
            holder.itemName.text = donation.items.entries.joinToString { "${it.key}: ${it.value}" }
        }

        override fun getItemCount() = donations.size
    }
}
