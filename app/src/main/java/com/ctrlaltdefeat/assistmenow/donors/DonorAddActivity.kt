package com.ctrlaltdefeat.assistmenow.donors

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.ctrlaltdefeat.assistmenow.R
import com.ctrlaltdefeat.assistmenow.data.Models

class DonorAddActivity : AppCompatActivity() {
    private lateinit var buttonBack: ImageView
    private lateinit var buttonAddItem: Button
    private lateinit var donationPosition: Models.DonationPosition
    private lateinit var donationItems: List<Models.DonationItem>

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)
        buttonAddItem = findViewById(R.id.b_additem)

        buttonBack.setOnClickListener {
            val intent = Intent(this, DonorMakeActivity::class.java)

            startActivity(intent)
        }

        buttonAddItem.setOnClickListener {
            for (i in donationItems.indices) {
                val item = donationItems[i]
                val viewHolder = findViewById<RecyclerView>(R.id.c_r).findViewHolderForAdapterPosition(i)
                val quantity = viewHolder?.itemView?.findViewById<NumberPicker>(R.id.recycler_ad_item_np)?.value ?: 0

                item.quantity = quantity
            }

            val dPosition: List<Models.DonationPosition> = listOf(donationPosition)
            val intent = Intent(this, DonorMakeActivity::class.java).apply {
                putParcelableArrayListExtra("donationAdded", ArrayList(donationItems))
                putParcelableArrayListExtra("donationAddedPosition", ArrayList(dPosition))
            }

            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.c_r)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = DonationItemAdapter(donationItems)
        recyclerView.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_add)

        donationItems = intent.getParcelableArrayListExtra<Models.DonationItem>("donationItems") ?: listOf()
        donationPosition = intent.getParcelableArrayListExtra<Models.DonationPosition>("donationPosition")
            ?.get(0)
            ?: Models.DonationPosition(0)

        initButtons()
        initRecyclerView()
    }

    inner class DonationItemAdapter(private val dItems: List<Models.DonationItem>) : RecyclerView.Adapter<DonationItemAdapter.ViewHolder>() {
        inner class ViewHolder(donationItemView: View) : RecyclerView.ViewHolder(donationItemView)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DonationItemAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_available_donation_items, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: DonationItemAdapter.ViewHolder, position: Int) {
            val donation = dItems[position]
            val numberPicker = holder.itemView.findViewById<NumberPicker>(R.id.recycler_ad_item_np)

            holder.itemView.findViewById<TextView>(R.id.recycler_ad_item_t).text = donation.item
            numberPicker.minValue = 0
            numberPicker.maxValue = 100
            numberPicker.value = donation.quantity
        }

        override fun getItemCount(): Int {
            return dItems.size
        }
    }
}