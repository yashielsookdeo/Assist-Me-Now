package com.wordapp.test2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FinalizeRequestsActivity : AppCompatActivity() {

    // In FinalizeRequestsActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finalize_requests)

        RequestTracker.getItems { items ->
            val adapter = FinalizeAdapter(items)
            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }


        val confirmButton: Button = findViewById(R.id.confirmButton)
        confirmButton.setOnClickListener {
            RequestTracker.finalizeRequests()  // Finalize the requests when Confirm button is clicked
        }

    }

    inner class FinalizeAdapter(private val items: List<RecipientActivity.RequestItem>) :
        RecyclerView.Adapter<FinalizeAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemName: TextView = view.findViewById(R.id.item_name)
            val removeButton: Button = view.findViewById(R.id.removeButton)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reqfinalize, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.itemName.text = item.name
        }

        override fun getItemCount() = items.size
    }

}
