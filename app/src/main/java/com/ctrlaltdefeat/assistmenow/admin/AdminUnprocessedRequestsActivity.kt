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

class AdminUnprocessedRequestsActivity : AppCompatActivity() {
    private lateinit var buttonBack: ImageView
    private lateinit var allRequests: List<Models.FinalRequests>
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

        val adapter = RequestsAdapter(allRequests)
        recyclerView.adapter = adapter
    }

    private fun initRequests(callback: (Boolean) -> Unit) {
        Firebase.getUnprocessedRequests() { success, requestsList ->
            if (success) {
                allRequests = requestsList

                callback(true)
            } else {
                callback(false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_unprocessed_requests)
        weirdContextThing = this

        initButtons()

        initRequests { success ->
            if (success) {
                initRecyclerView()
            } else {
                Toast.makeText(this, "Failed to get all requests - please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }

    inner class RequestsAdapter(private val requests: List<Models.FinalRequests>) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {
        inner class ViewHolder(requestsView: View) : RecyclerView.ViewHolder(requestsView)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RequestsAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_unprocessed_requests, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RequestsAdapter.ViewHolder, position: Int) {
            val request = requests[position]

            var text = request.name + "\n" + request.creator + "\n"

            for (item in request.request) {
                text = text + item.item + "\n"
            }

            holder.itemView.findViewById<TextView>(R.id.recycler_ur_t).text = text
            holder.itemView.findViewById<Button>(R.id.recycler_ur_item_b_accept).setOnClickListener {
                Firebase.acceptRequest(request.uid) { success, message ->
                    if (success) {
                        initRequests { initSuccess ->
                            if (initSuccess) {
                                val recyclerView: RecyclerView = findViewById(R.id.c_r)
                                val adapter = RequestsAdapter(allRequests)
                                recyclerView.adapter = adapter

                                Toast.makeText(weirdContextThing, message, Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(weirdContextThing, "Failed to get all requests - please try again.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(weirdContextThing, "${message} - Please try again", Toast.LENGTH_LONG).show()
                    }
                }
            }

            holder.itemView.findViewById<Button>(R.id.recycler_ur_item_b_cancel).setOnClickListener {
                Firebase.removeRequest(request.uid) { success, message ->
                    if (success) {
                        initRequests { initSuccess ->
                            if (initSuccess) {
                                val recyclerView: RecyclerView = findViewById(R.id.c_r)
                                val adapter = RequestsAdapter(allRequests)
                                recyclerView.adapter = adapter

                                Toast.makeText(weirdContextThing, message, Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(weirdContextThing, "Failed to get all requests - please try again.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(weirdContextThing, "${message} - Please try again", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return requests.size
        }
    }
}