package com.wordapp.test2.Donors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wordapp.test2.R
import com.wordapp.test2.databinding.ActivityItemsBinding

class ItemsActivity : AppCompatActivity() {

    private lateinit var items: List<DonorActivity.DonateItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        items = intent.getParcelableArrayListExtra<DonorActivity.DonateItem>("items") ?: listOf()

        val adapter = DonateAdapter(items)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.buttonAddToDonation.setOnClickListener {
            // Iterate through all items in the list
            for (position in items.indices) {
                val item = items[position]
                // Get the ViewHolder for the current position
                val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(position)
                // Get the quantity from the NumberPicker in the ViewHolder
                val quantity = viewHolder?.itemView?.findViewById<NumberPicker>(R.id.number_picker)?.value ?: 0
                if (quantity > 0) {  // Only add the item if the quantity is greater than 0
                    DonationTracker.addItem(item.name, quantity)
                }
            }
        }


        binding.buttonBack.setOnClickListener {
            finish()
        }

    }


    inner class DonateAdapter(private val items: List<DonorActivity.DonateItem>) : RecyclerView.Adapter<DonateAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemName: TextView = view.findViewById(R.id.item_name)
            val numberPicker: NumberPicker = view.findViewById(R.id.number_picker)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donate, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.itemName.text = item.name
            holder.numberPicker.minValue = 0
            holder.numberPicker.maxValue = 100  // Set a maximum value as per your requirement
        }

        override fun getItemCount() = items.size
    }

}
