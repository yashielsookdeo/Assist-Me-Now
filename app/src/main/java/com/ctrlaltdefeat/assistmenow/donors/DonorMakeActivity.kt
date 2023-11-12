package com.ctrlaltdefeat.assistmenow.donors

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Display.Mode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ctrlaltdefeat.assistmenow.R
import com.ctrlaltdefeat.assistmenow.data.Models
import com.ctrlaltdefeat.assistmenow.objects.Donation

class DonorMakeActivity : AppCompatActivity() {
    private lateinit var buttonBack: ImageView
    private lateinit var buttonCreateDonation: Button
    private lateinit var buttonDropOff: Button
    private lateinit var donations: List<Models.AvailableDonation>

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)
        buttonDropOff = findViewById(R.id.b_dropoff)
        buttonCreateDonation = findViewById(R.id.b_createdonation)

        buttonBack.setOnClickListener {
            val intent = Intent(this, DonorDashboardActivity::class.java)

            startActivity(intent)
        }

        buttonDropOff.setOnClickListener {
            val intent = Intent(this, DonorDropOffActivity::class.java)

            startActivity(intent)
        }

        buttonCreateDonation.setOnClickListener {
            val tempDonations = Donation.getItems()
            var isThereOneItemWithQuantity = false

            for (i in tempDonations) {
                for (m in i.items) {
                    if (m.quantity > 0) {
                        isThereOneItemWithQuantity = true

                        break
                    }
                }

                if (isThereOneItemWithQuantity) {
                    break
                }
            }

            if (!isThereOneItemWithQuantity) {
                Toast.makeText(this, "You haven't specified a quantity for any donation item.", Toast.LENGTH_LONG).show()
            } else {
                val dropOffLong = Donation.getDropOffLong()
                val dropOffLat = Donation.getDropOffLat()
                var isDropOffValid = false

                if (dropOffLong != 0.0 || dropOffLat != 0.0) {
                    isDropOffValid = true
                }

                if (!isDropOffValid) {
                    Toast.makeText(this, "You haven't chosen a drop off location yet.", Toast.LENGTH_LONG).show()
                } else {
                    val donationName = findViewById<EditText>(R.id.input_name).text.toString()

                    if (donationName.isEmpty()) {
                        Toast.makeText(this, "The donation name is invalid.", Toast.LENGTH_LONG).show()
                    } else {
                        Donation.setName(donationName)

                        val intent = Intent(this, DonorFinalizeActivity::class.java)

                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.c_r)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = DonationAdapter(donations)
        recyclerView.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_make)

        if (intent.getParcelableArrayListExtra<Models.DonationItem>("donationAdded") != null) {
            var tempDonations = Donation.getItems()
            val dPosition = intent.getParcelableArrayListExtra<Models.DonationPosition>("donationAddedPosition")
                ?.get(0)
                ?: Models.DonationPosition(0)

            val dItems = intent.getParcelableArrayListExtra<Models.DonationItem>("donationAdded")

            if (dItems != null) {
                tempDonations[dPosition.position].items = dItems
            }

            donations = tempDonations
            Donation.setItems(tempDonations)
        } else {
            var tempDonations = Donation.getItems()

            if (tempDonations.isEmpty()) {
                tempDonations = listOf(
                    Models.AvailableDonation("Grains", listOf(Models.DonationItem("Rice", 0), Models.DonationItem("Wheat", 0))),
                    Models.AvailableDonation("Canned Foods", listOf(Models.DonationItem("Baked Beans", 0), Models.DonationItem("Sweet Corn", 0))),
                    Models.AvailableDonation("Baking", listOf(Models.DonationItem("Flour", 0), Models.DonationItem("Sugar", 0))),
                )
            }

            donations = tempDonations
            Donation.setItems(tempDonations)
        }

        initButtons()
        initRecyclerView()
    }

    inner class DonationAdapter(private val availableDonations: List<Models.AvailableDonation>) : RecyclerView.Adapter<DonationAdapter.ViewHolder>() {
        inner class ViewHolder(donationView: View) : RecyclerView.ViewHolder(donationView), View.OnClickListener {
            init {
                donationView.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val donation = availableDonations[position]
                    val donationPosition: List<Models.DonationPosition> = listOf(Models.DonationPosition(position))
                    val intent = Intent(this@DonorMakeActivity, DonorAddActivity::class.java).apply {
                        putParcelableArrayListExtra("donationPosition", ArrayList(donationPosition))
                        putParcelableArrayListExtra("donationItems", ArrayList(donation.items))
                    }

                    startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DonationAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_available_donations, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: DonationAdapter.ViewHolder, position: Int) {
            val donation = availableDonations[position]

            holder.itemView.findViewById<TextView>(R.id.recycler_ad_t).text = donation.name
        }

        override fun getItemCount(): Int {
            return availableDonations.size
        }
    }
}