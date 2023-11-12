package com.ctrlaltdefeat.assistmenow.recipients

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ctrlaltdefeat.assistmenow.R
import com.ctrlaltdefeat.assistmenow.data.Models
import com.ctrlaltdefeat.assistmenow.database.Firebase

class RecipientRequestsActivity : AppCompatActivity() {
    private lateinit var buttonBack: ImageView
    private lateinit var myRequests: List<Models.FinalRequests>

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)

        buttonBack.setOnClickListener {
            val intent = Intent(this, RecipientDashboardActivity::class.java)

            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.c_r)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = RequestAdapter(myRequests)
        recyclerView.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient_requests)

        initButtons()

        Firebase.getRequestsByUser() { success, requestsList ->
            if (success) {
                myRequests = requestsList

                initRecyclerView()
            } else {
                Toast.makeText(this, "Failed to get your requests - please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }

    inner class RequestAdapter(private val requests: List<Models.FinalRequests>) : RecyclerView.Adapter<RequestAdapter.ViewHolder>() {
        inner class ViewHolder(requestView: View) : RecyclerView.ViewHolder(requestView)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RequestAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_request_items, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RequestAdapter.ViewHolder, position: Int) {
            val request = requests[position]

            holder.itemView.findViewById<TextView>(R.id.recycler_ri_t).text = request.name

            if (request.processed) {
                holder.itemView.findViewById<TextView>(R.id.recycler_ri_tp).text = "Processed"
            } else {
                holder.itemView.findViewById<TextView>(R.id.recycler_ri_tp).text = "Being Processed"
            }
        }

        override fun getItemCount(): Int {
            return requests.size
        }
    }
}