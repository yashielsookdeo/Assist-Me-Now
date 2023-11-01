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
import com.wordapp.test2.R

class AdminOngoingDon : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var donationsAdapter: DonationsAdapter
    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            updateDonationsFromSnapshot(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle database error
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_ongoing_don)

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

        donationsAdapter = DonationsAdapter(mutableListOf())
        recyclerView.adapter = donationsAdapter
    }

    private fun updateDonationsFromSnapshot(dataSnapshot: DataSnapshot) {
        val donationsList = mutableListOf<Donation>()
        for (userSnapshot in dataSnapshot.children) {
            val donorUserId = userSnapshot.key ?: ""
            for (donationSnapshot in userSnapshot.child("finalizedDonations").children) {
                val itemsList = mutableListOf<Item>()
                for (itemSnapshot in donationSnapshot.children) {
                    val itemName = itemSnapshot.key ?: ""
                    val itemQuantity = itemSnapshot.getValue(Long::class.java) ?: 0
                    itemsList.add(Item(itemName, itemQuantity))
                }
                val donationId = donationSnapshot.key ?: ""
                donationsList.add(Donation(donationId, itemsList, donorUserId = donorUserId))
            }
        }
        donationsAdapter.updateDonations(donationsList)
    }

    private fun fetchDonations() {
        Log.d("AdminOngoingDon", "fetchDonations called")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("AdminOngoingDon", "onDataChange triggered")
                val donationsList = mutableListOf<Donation>()
                for (userSnapshot in dataSnapshot.children) {
                    val donorUserId = userSnapshot.key ?: ""
                    for (donationSnapshot in userSnapshot.child("finalizedDonations").children) {
                        val itemsList = mutableListOf<Item>()
                        for (itemSnapshot in donationSnapshot.children) {
                            val itemName = itemSnapshot.key ?: ""
                            val itemQuantity = itemSnapshot.getValue(Long::class.java) ?: 0
                            itemsList.add(Item(itemName, itemQuantity))
                        }
                        val donationId = donationSnapshot.key ?: ""
                        donationsList.add(Donation(donationId, itemsList, donorUserId = donorUserId))
                    }
                }
                Log.d("AdminOngoingDon", "Updating donations adapter with new data")
                donationsAdapter.updateDonations(donationsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }



    data class Item(
        val name: String = "",
        val quantity: Long = 0
    )

    data class Donation(
        val id: String = "",
        val items: List<Item> = listOf(),
        val status: String = "Finalized",  // Default to "Finalized"
        val donorUserId: String = ""
    )

    fun moveDonationToCompleted(donation: Donation) {
        Log.d("AdminOngoingDon", "moveDonationToCompleted called with donation: $donation")
        val database = FirebaseDatabase.getInstance().getReference("users")

        // First, add the donation to CompletedDonations under the correct donor's node
        database.child(donation.donorUserId).child("CompletedDonations").child(donation.id)
            .setValue(donation.copy(status = "Completed"))
            .addOnSuccessListener {
                Log.d("AdminOngoingDon", "Successfully added donation to CompletedDonations")

                // On successful addition, remove the donation from FinalizedDonations
                database.child(donation.donorUserId).child("finalizedDonations").child(donation.id).removeValue()
                    .addOnSuccessListener {
                        Log.d("AdminOngoingDon", "Successfully removed donation from FinalizedDonations")
                    }
                    .addOnFailureListener {
                        Log.e("AdminOngoingDon", "Failed to remove donation from FinalizedDonations", it)
                    }
            }
            .addOnFailureListener {
                Log.e("AdminOngoingDon", "Failed to add donation to CompletedDonations", it)
            }


}




    inner class DonationsAdapter(private val donations: MutableList<Donation>) : RecyclerView.Adapter<DonationsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.adminongoingdonview, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val donation = donations[position]
            val itemsString = donation.items.joinToString { "${it.name}: ${it.quantity}" }
            holder.itemName.text = "User ID: ${donation.donorUserId}\nItems: $itemsString"

            holder.acceptButton.setOnClickListener {
                Log.d("AdminOngoingDon", "Accept button clicked for donation: $donation")
                moveDonationToCompleted(donation)
            }

        }

        fun updateDonations(newDonations: List<Donation>) {
            Log.d("AdminOngoingDon", "updateDonations called with new data: $newDonations")
            donations.clear()
            donations.addAll(newDonations)
            notifyDataSetChanged()
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemName: TextView = view.findViewById(R.id.item_name)
            val acceptButton: Button = view.findViewById(R.id.accept_button)
        }

        override fun getItemCount() = donations.size
    }
}
