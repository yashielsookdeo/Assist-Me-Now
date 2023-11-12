package com.ctrlaltdefeat.assistmenow.donors

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.ctrlaltdefeat.assistmenow.objects.Donation

class DonorFinalizeActivity : AppCompatActivity() {
    private lateinit var buttonBack: ImageView
    private lateinit var buttonCancel: Button
    private lateinit var buttonMakeDonation: Button
    private lateinit var donations: List<Models.FinalDonation>

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)
        buttonCancel = findViewById(R.id.b_canceldonationfinal)
        buttonMakeDonation = findViewById(R.id.b_makedonationfinal)

        buttonBack.setOnClickListener {
            val intent = Intent(this, DonorMakeActivity::class.java)

            startActivity(intent)
        }

        buttonCancel.setOnClickListener {
            val intent = Intent(this, DonorMakeActivity::class.java)

            startActivity(intent)
        }

        buttonMakeDonation.setOnClickListener {
            if (donations.isEmpty()) {
                Toast.makeText(this, "You have no items in your donation", Toast.LENGTH_LONG).show()
            } else {
                Firebase.addDonation(donations, Donation.getDropOffLong(), Donation.getDropOffLat(), Donation.getName()) { success, message ->
                    if (success) {
                        Donation.resetItems()

                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                        val intent = Intent(this, DonorDashboardActivity::class.java)

                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "${message}. Please try again", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.c_r)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = FinalizeDonationAdapter(donations)
        recyclerView.adapter = adapter
    }

    private fun initDonations() {
        val tempDonations = Donation.getItems()

        donations = listOf()

        for (i in tempDonations.indices) {
            val availableDonation = tempDonations[i]

            for (m in availableDonation.items.indices) {
                val item = availableDonation.items[m]

                if (item.quantity > 0) {
                    val finalDonation = Models.FinalDonation(item.item, item.quantity, i, m)

                    donations = donations + finalDonation
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_finalize)

        initDonations()
        initButtons()
        initRecyclerView()
    }

    inner class FinalizeDonationAdapter(private val finalDonations: List<Models.FinalDonation>) : RecyclerView.Adapter<FinalizeDonationAdapter.ViewHolder>() {
        inner class ViewHolder(finalDonationView: View) : RecyclerView.ViewHolder(finalDonationView)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): FinalizeDonationAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_finalize_donations, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: FinalizeDonationAdapter.ViewHolder, position: Int) {
            val donation = finalDonations[position]
            val text = donation.item + " | " + donation.quantity

            holder.itemView.findViewById<TextView>(R.id.recycler_fd_item_t).text = text
            holder.itemView.findViewById<Button>(R.id.recycler_fd_item_b).setOnClickListener {
                Donation.setDonationItemQuantity(donation.AvailableDonationID, donation.DonationItemID, 0)

                initDonations()

                val recyclerView: RecyclerView = findViewById(R.id.c_r)
                val adapter = FinalizeDonationAdapter(donations)
                recyclerView.adapter = adapter
            }
        }

        override fun getItemCount(): Int {
            return finalDonations.size
        }
    }
}