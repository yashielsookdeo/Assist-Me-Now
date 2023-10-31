package com.wordapp.test2.Donors

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
import com.wordapp.test2.R
import com.wordapp.test2.databinding.ActivityFinalizeDonationBinding

class FinalizeDonationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinalizeDonationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinalizeDonationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DonationTracker.getItems { items ->
            val adapter = FinalizeAdapter(items)
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
            // Handle "Confirm" button click
            binding.confirmButton.setOnClickListener {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId =
                    currentUser?.uid ?: return@setOnClickListener  // Get the current user's uid

                val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
                val donationsRef = userRef.child("donations")
                val finalizedDonationsRef = userRef.child("finalizedDonations")

                // Retrieve the donation data
                donationsRef.get().addOnSuccessListener { snapshot ->
                    val donations = snapshot.value as? Map<String, Long>
                    if (donations != null && donations.isNotEmpty()) {
                        // Create a new entry under finalizedDonations
                        val newFinalizedDonationRef = finalizedDonationsRef.push()
                        newFinalizedDonationRef.setValue(donations).addOnSuccessListener {
                            // If the data was copied successfully, delete the old data
                            donationsRef.removeValue().addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Donation finalized successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "Failed to remove old donations: ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Failed to finalize donation: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(this, "No donations to finalize.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Failed to retrieve donations: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


            inner class FinalizeAdapter(private val items: Map<String, Long>) :
        RecyclerView.Adapter<FinalizeAdapter.ViewHolder>() {


        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemName: TextView = view.findViewById(R.id.item_name)
            val itemQuantity: TextView = view.findViewById(R.id.item_quantity)
            val removeButton: Button = view.findViewById(R.id.removeButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_finalize, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items.keys.elementAt(position)
            val quantity = items[item]
            holder.itemName.text = item
            holder.itemQuantity.text = quantity.toString()

            // Set a click listener for the "Remove" button
            holder.removeButton.setOnClickListener {
                // Remove the item from the database
                DonationTracker.removeItem(item)

                // Now, refresh the items in the RecyclerView
                DonationTracker.getItems { updatedItems ->
                    // This line creates a new FinalizeAdapter with the updated items
                    // and sets it to the RecyclerView, refreshing the displayed items.
                    binding.recyclerView.adapter = FinalizeAdapter(updatedItems)
                }
            }
        }

        override fun getItemCount() = items.size
    }

}
