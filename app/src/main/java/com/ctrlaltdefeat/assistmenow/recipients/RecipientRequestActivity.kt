package com.ctrlaltdefeat.assistmenow.recipients

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.ctrlaltdefeat.assistmenow.objects.Request

class RecipientRequestActivity : AppCompatActivity() {
    private lateinit var buttonBack: ImageView
    private lateinit var buttonPickUp: Button
    private lateinit var buttonCreateRequest: Button
    private lateinit var requests: List<Models.AvailableRequest>

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)
        buttonPickUp = findViewById(R.id.b_pickup)
        buttonCreateRequest = findViewById(R.id.b_createrequest)

        buttonBack.setOnClickListener {
            val intent = Intent(this, RecipientDashboardActivity::class.java)

            startActivity(intent)
        }

        buttonPickUp.setOnClickListener {
            val intent = Intent(this, RecipientPickUpActivity::class.java)

            startActivity(intent)
        }

        buttonCreateRequest.setOnClickListener {
            val tempRequests = Request.getItems()
            var isThereOneItemSelected = false

            for (i in tempRequests) {
                for (m in i.items) {
                    if (m.selected) {
                        isThereOneItemSelected = true

                        break
                    }
                }

                if (isThereOneItemSelected) {
                    break
                }
            }

            if (!isThereOneItemSelected) {
                Toast.makeText(this, "You haven't specified an item for any donation.", Toast.LENGTH_LONG).show()
            } else {
                val pickUpLong = Request.getPickUpLong()
                val pickUpLat = Request.getPickUpLat()
                var isPickUpValid = false

                if (pickUpLong != 0.0 || pickUpLat != 0.0) {
                    isPickUpValid = true
                }

                if (!isPickUpValid) {
                    Toast.makeText(this, "You haven't chosen a pick up location yet.", Toast.LENGTH_LONG).show()
                } else {
                    val requestName = findViewById<EditText>(R.id.input_name).text.toString()

                    if (requestName.isEmpty()) {
                        Toast.makeText(this, "The request name is invalid.", Toast.LENGTH_LONG).show()
                    } else {
                        Request.setName(requestName)

                        val intent = Intent(this, RecipientFinalizeActivity::class.java)

                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.c_r)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = RequestAdapter(requests)
        recyclerView.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient_request)

        if (intent.getParcelableArrayListExtra<Models.RequestItem>("requestAdded") != null) {
            var tempRequests = Request.getItems()
            val rPosition = intent.getParcelableArrayListExtra<Models.RequestPosition>("requestAddedPosition")
                ?.get(0)
                ?: Models.RequestPosition(0)

            val rItems = intent.getParcelableArrayListExtra<Models.RequestItem>("requestAdded")

            if (rItems != null) {
                tempRequests[rPosition.position].items = rItems
            }

            requests = tempRequests
            Request.setItems(tempRequests)
        } else {
            var tempRequests = Request.getItems()

            if (tempRequests.isEmpty()) {
                tempRequests = listOf(
                    Models.AvailableRequest("Grains", listOf(Models.RequestItem("Rice", false), Models.RequestItem("Wheat", false))),
                    Models.AvailableRequest("Canned Foods", listOf(Models.RequestItem("Baked Beans", false), Models.RequestItem("Sweet Corn", false))),
                    Models.AvailableRequest("Baking", listOf(Models.RequestItem("Flour", false), Models.RequestItem("Sugar", false))),
                )
            }

            requests = tempRequests
            Request.setItems(tempRequests)
        }

        initButtons()
        initRecyclerView()
    }

    inner class RequestAdapter(private val availableRequests: List<Models.AvailableRequest>) : RecyclerView.Adapter<RequestAdapter.ViewHolder>() {
        inner class ViewHolder(requestView: View) : RecyclerView.ViewHolder(requestView), View.OnClickListener {
            init {
                requestView.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val request = availableRequests[position]
                    val requestPosition: List<Models.RequestPosition> = listOf(Models.RequestPosition(position))

                    val intent = Intent(this@RecipientRequestActivity, RecipientAddActivity::class.java).apply {
                        putParcelableArrayListExtra("requestPosition", ArrayList(requestPosition))
                        putParcelableArrayListExtra("requestItems", ArrayList(request.items))
                    }

                    startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RequestAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_available_requests, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RequestAdapter.ViewHolder, position: Int) {
            val request = availableRequests[position]

            holder.itemView.findViewById<TextView>(R.id.recycler_ar_t).text = request.name
        }

        override fun getItemCount(): Int {
            return availableRequests.size
        }
    }
}