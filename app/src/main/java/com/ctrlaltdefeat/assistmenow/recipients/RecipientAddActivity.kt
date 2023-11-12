package com.ctrlaltdefeat.assistmenow.recipients

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ctrlaltdefeat.assistmenow.R
import com.ctrlaltdefeat.assistmenow.data.Models

class RecipientAddActivity : AppCompatActivity() {
    private lateinit var buttonBack: ImageView
    private lateinit var buttonAddItem: Button
    private lateinit var requestPosition: Models.RequestPosition
    private lateinit var requestItems: List<Models.RequestItem>

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)
        buttonAddItem = findViewById(R.id.b_additem)

        buttonBack.setOnClickListener {
            val intent = Intent(this, RecipientRequestActivity::class.java)

            startActivity(intent)
        }

        buttonAddItem.setOnClickListener {
            for (i in requestItems.indices) {
                val item = requestItems[i]
                val viewHolder = findViewById<RecyclerView>(R.id.c_r).findViewHolderForAdapterPosition(i)
                val selected = viewHolder?.itemView?.findViewById<CheckBox>(R.id.recycler_ar_item_cb)?.isChecked ?: false

                item.selected = selected
            }

            val rPosition: List<Models.RequestPosition> = listOf(requestPosition)
            val intent = Intent(this, RecipientRequestActivity::class.java).apply {
                putParcelableArrayListExtra("requestAdded", ArrayList(requestItems))
                putParcelableArrayListExtra("requestAddedPosition", ArrayList(rPosition))
            }

            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.c_r)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = RequestItemAdapter(requestItems)
        recyclerView.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient_add)

        requestItems = intent.getParcelableArrayListExtra<Models.RequestItem>("requestItems") ?: listOf()
        requestPosition = intent.getParcelableArrayListExtra<Models.RequestPosition>("requestPosition")
            ?.get(0)
            ?: Models.RequestPosition(0)

        initButtons()
        initRecyclerView()
    }

    inner class RequestItemAdapter(private val rItems: List<Models.RequestItem>) : RecyclerView.Adapter<RequestItemAdapter.ViewHolder>() {
        inner class ViewHolder(requestItemView: View) : RecyclerView.ViewHolder(requestItemView)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RequestItemAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_available_requests_items, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RequestItemAdapter.ViewHolder, position: Int) {
            val request = rItems[position]
            val checkBox = holder.itemView.findViewById<CheckBox>(R.id.recycler_ar_item_cb)

            holder.itemView.findViewById<TextView>(R.id.recycler_ar_item_t).text = request.item
            checkBox.isChecked = request.selected
        }

        override fun getItemCount(): Int {
            return rItems.size
        }
    }
}