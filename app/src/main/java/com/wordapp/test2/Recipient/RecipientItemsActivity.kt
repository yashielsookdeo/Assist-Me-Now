package com.wordapp.test2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecipientItemsActivity : AppCompatActivity() {

    private lateinit var items: List<RecipientActivity.RequestItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient_items)

        items = intent.getParcelableArrayListExtra<RecipientActivity.RequestItem>("items") ?: listOf()

        val adapter = RequestAdapter(items)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val addToRequestButton: Button = findViewById(R.id.button_add_to_request)  // Renamed button
        addToRequestButton.setOnClickListener {
            val requestedItems = items.filter { it.requested }
            if (requestedItems.isNotEmpty()) {
                RequestTracker.addItems(requestedItems)
                Toast.makeText(this, "Items added to request", Toast.LENGTH_SHORT).show()
            }
        }

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }



    }

    inner class RequestAdapter(private val items: List<RecipientActivity.RequestItem>) : RecyclerView.Adapter<RequestAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val itemName: TextView = itemView.findViewById(R.id.item_name)
            val requestCheckBox: CheckBox = itemView.findViewById(R.id.request_checkbox)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.itemName.text = item.name
            holder.requestCheckBox.isChecked = item.requested

            holder.requestCheckBox.setOnCheckedChangeListener { _, isChecked ->
                item.requested = isChecked
            }
        }

        override fun getItemCount() = items.size
    }
}
