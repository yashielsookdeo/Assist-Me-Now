package com.ctrlaltdefeat.assistmenow.donors

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ctrlaltdefeat.assistmenow.R
import com.ctrlaltdefeat.assistmenow.data.Models
import com.ctrlaltdefeat.assistmenow.database.Firebase
import com.ctrlaltdefeat.assistmenow.objects.Donation

class DonorDonationsActivity : AppCompatActivity() {
    private lateinit var buttonBack: ImageView
    private lateinit var myDonations: List<Models.FinalDonations>

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)

        buttonBack.setOnClickListener {
            val intent = Intent(this, DonorDashboardActivity::class.java)

            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.c_r)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = DonationAdapter(myDonations)
        recyclerView.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_donations)

        initButtons()

        Firebase.getDonationsByUser() { success, donationsList ->
            if (success) {
                myDonations = donationsList

                initRecyclerView()
            } else {
                Toast.makeText(this, "Failed to get your donations - please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }

    inner class DonationAdapter(private val donations: List<Models.FinalDonations>) : RecyclerView.Adapter<DonationAdapter.ViewHolder>() {
        inner class ViewHolder(donationView: View) : RecyclerView.ViewHolder(donationView)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DonationAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_donation_items, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: DonationAdapter.ViewHolder, position: Int) {
            val donation = donations[position]

            holder.itemView.findViewById<TextView>(R.id.recycler_di_t).text = donation.name

            if (donation.processed) {
                holder.itemView.findViewById<TextView>(R.id.recycler_di_tp).text = "Processed"
            } else {
                holder.itemView.findViewById<TextView>(R.id.recycler_di_tp).text = "Being Processed"
            }
        }

        override fun getItemCount(): Int {
            return donations.size
        }
    }
}