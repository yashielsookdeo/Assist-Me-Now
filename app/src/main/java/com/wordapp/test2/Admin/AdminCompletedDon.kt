package com.wordapp.test2.Admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.wordapp.test2.R


    class AdminCompletedDon : AppCompatActivity() {
            private lateinit var databaseReference: DatabaseReference
            private lateinit var adapter: CompletedDonationsAdapter

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_admin_completed_don)

                databaseReference = FirebaseDatabase.getInstance().getReference("users")
                setupRecyclerView()
                fetchCompletedDonations()
            }

            private fun setupRecyclerView() {
                val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                recyclerView.layoutManager = LinearLayoutManager(this)
                adapter = CompletedDonationsAdapter(mutableListOf())
                recyclerView.adapter = adapter
            }

            private fun fetchCompletedDonations() {
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val completedDonationsList = mutableListOf<CompletedDonation>()
                        for (userSnapshot in dataSnapshot.children) {
                            val uid = userSnapshot.key ?: ""
                            for (donationSnapshot in userSnapshot.child("CompletedDonations").children) {
                                // Assuming donation data is a map
                                val donationData = donationSnapshot.value as? Map<String, Any> ?: continue
                                completedDonationsList.add(CompletedDonation(uid, donationData))
                            }
                        }
                        adapter.updateDonations(completedDonationsList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle database error
                    }
                })
            }

            data class CompletedDonation(val uid: String, val donationData: Map<String, Any>)

            class CompletedDonationsAdapter(private val donations: MutableList<CompletedDonation>) : RecyclerView.Adapter<CompletedDonationsAdapter.ViewHolder>() {

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_completed_donation, parent, false)
                    return ViewHolder(view)
                }

                override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                    val donation = donations[position]
                    holder.userId.text = donation.uid
                    holder.donationData.text = donation.donationData.toString()
                }

                override fun getItemCount() = donations.size

                fun updateDonations(newDonations: List<CompletedDonation>) {
                    donations.clear()
                    donations.addAll(newDonations)
                    notifyDataSetChanged()
                }

                inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                    val userId: TextView = view.findViewById(R.id.userId)
                    val donationData: TextView = view.findViewById(R.id.donationData)
                }
            }
        }
