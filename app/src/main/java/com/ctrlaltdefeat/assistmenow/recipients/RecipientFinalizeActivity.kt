package com.ctrlaltdefeat.assistmenow.recipients

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
import com.ctrlaltdefeat.assistmenow.objects.Request

class RecipientFinalizeActivity : AppCompatActivity() {
    private lateinit var buttonBack: ImageView
    private lateinit var buttonCancel: Button
    private lateinit var buttonMakeRequest: Button
    private lateinit var requests: List<Models.FinalRequest>

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)
        buttonCancel = findViewById(R.id.b_cancelrequestfinal)
        buttonMakeRequest = findViewById(R.id.b_makerequestfinal)

        buttonBack.setOnClickListener {
            val intent = Intent(this, RecipientRequestActivity::class.java)

            startActivity(intent)
        }

        buttonCancel.setOnClickListener {
            val intent = Intent(this, RecipientRequestActivity::class.java)

            startActivity(intent)
        }

        buttonMakeRequest.setOnClickListener {
            if (requests.isEmpty()) {
                Toast.makeText(this, "You have no items in your request", Toast.LENGTH_LONG).show()
            } else {
                Firebase.addRequest(requests, Request.getPickUpLong(), Request.getPickUpLat(), Request.getName()) { success, message ->
                    if (success) {
                        Request.resetItems()

                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                        val intent = Intent(this, RecipientDashboardActivity::class.java)

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

        val adapter = FinalizeRequestAdapter(requests)
        recyclerView.adapter = adapter
    }

    private fun initRequests() {
        val tempRequests = Request.getItems()

        requests = listOf()

        for (i in tempRequests.indices) {
            val availableRequest = tempRequests[i]

            for (m in availableRequest.items.indices) {
                val item = availableRequest.items[m]

                if (item.selected) {
                    val finalRequest = Models.FinalRequest(item.item, item.selected, i, m)

                    requests = requests + finalRequest
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient_finalize)

        initRequests()
        initRecyclerView()
        initButtons()
    }

    inner class FinalizeRequestAdapter(private val finalRequest: List<Models.FinalRequest>) : RecyclerView.Adapter<FinalizeRequestAdapter.ViewHolder>() {
        inner class ViewHolder(finalRequestView: View) : RecyclerView.ViewHolder(finalRequestView)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): FinalizeRequestAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_finalize_request, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: FinalizeRequestAdapter.ViewHolder, position: Int) {
            val request = finalRequest[position]
            val text = request.item

            holder.itemView.findViewById<TextView>(R.id.recycler_fr_item_t).text = text
            holder.itemView.findViewById<Button>(R.id.recycler_fr_item_b).setOnClickListener {
                Request.setRequestItemSelected(request.AvailableRequestID, request.RequestItemID, false)

                initRequests()

                val recyclerView: RecyclerView = findViewById(R.id.c_r)
                val adapter = FinalizeRequestAdapter(requests)
                recyclerView.adapter = adapter
            }
        }

        override fun getItemCount(): Int {
            return finalRequest.size
        }
    }
}