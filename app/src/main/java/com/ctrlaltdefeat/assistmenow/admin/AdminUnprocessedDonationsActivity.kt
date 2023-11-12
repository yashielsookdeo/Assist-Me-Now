package com.ctrlaltdefeat.assistmenow.admin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ctrlaltdefeat.assistmenow.R
import com.ctrlaltdefeat.assistmenow.data.Models
import com.ctrlaltdefeat.assistmenow.database.Firebase
import com.ctrlaltdefeat.assistmenow.donors.DonorDashboardActivity
import com.ctrlaltdefeat.assistmenow.objects.Donation

class AdminUnprocessedDonationsActivity : AppCompatActivity() {
    private lateinit var buttonBack: ImageView
    private lateinit var allDonations: List<Models.FinalDonations>
    private lateinit var weirdContextThing: Context

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)

        buttonBack.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)

            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.c_r)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = DonationsAdapter(allDonations)
        recyclerView.adapter = adapter
    }

    private fun initDonations(callback: (Boolean) -> Unit) {
        Firebase.getUnprocessedDonations() { success, donationsList ->
            if (success) {
                allDonations = donationsList

                callback(true)
            } else {
                callback(false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_unprocessed_donations)
        weirdContextThing = this

        initButtons()

        initDonations { success ->
            if (success) {
                initRecyclerView()
            } else {
                Toast.makeText(this, "Failed to get all donations - please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }

    inner class DonationsAdapter(private val donations: List<Models.FinalDonations>) : RecyclerView.Adapter<DonationsAdapter.ViewHolder>() {
        inner class ViewHolder(donationsView: View) : RecyclerView.ViewHolder(donationsView)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DonationsAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_unprocessed_donations, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: DonationsAdapter.ViewHolder, position: Int) {
            val donation = donations[position]

            var text = donation.name + "\n" + donation.creator + "\n"

            for (item in donation.donation) {
                text = text + item.item + " | " + item.quantity + "\n"
            }

            holder.itemView.findViewById<TextView>(R.id.recycler_ud_t).text = text
            holder.itemView.findViewById<Button>(R.id.recycler_ud_item_b_accept).setOnClickListener {
                Firebase.acceptDonation(donation.uid) { success, message ->
                    if (success) {
                        initDonations { initSuccess ->
                            if (initSuccess) {
                                val recyclerView: RecyclerView = findViewById(R.id.c_r)
                                val adapter = DonationsAdapter(allDonations)
                                recyclerView.adapter = adapter

                                Toast.makeText(weirdContextThing, message, Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(weirdContextThing, "Failed to get all donations - please try again.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(weirdContextThing, "${message} - Please try again", Toast.LENGTH_LONG).show()
                    }
                }
            }

            holder.itemView.findViewById<Button>(R.id.recycler_ud_item_b_cancel).setOnClickListener {
                Firebase.removeDonation(donation.uid) { success, message ->
                    if (success) {
                        initDonations { initSuccess ->
                            if (initSuccess) {
                                val recyclerView: RecyclerView = findViewById(R.id.c_r)
                                val adapter = DonationsAdapter(allDonations)
                                recyclerView.adapter = adapter

                                Toast.makeText(weirdContextThing, message, Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(weirdContextThing, "Failed to get all donations - please try again.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(weirdContextThing, "${message} - Please try again", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return donations.size
        }
    }
}