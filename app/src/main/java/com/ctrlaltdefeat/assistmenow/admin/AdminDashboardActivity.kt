package com.ctrlaltdefeat.assistmenow.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ctrlaltdefeat.assistmenow.R
import com.ctrlaltdefeat.assistmenow.donors.DonorDonationsActivity
import com.ctrlaltdefeat.assistmenow.donors.DonorMakeActivity

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var buttonUnprocessedDonations: Button
    private lateinit var buttonUnprocessedRequests: Button
    private lateinit var buttonProcessedDonations: Button
    private lateinit var buttonProcessedRequests: Button

    private fun initButtons() {
        buttonUnprocessedDonations = findViewById(R.id.b_unprocesseddonations)
        buttonUnprocessedRequests = findViewById(R.id.b_unprocessedrequests)
        buttonProcessedDonations = findViewById(R.id.b_processeddonations)
        buttonProcessedRequests = findViewById(R.id.b_processedrequests)

        buttonUnprocessedDonations.setOnClickListener {
            val intent = Intent(this, AdminUnprocessedDonationsActivity::class.java)

            startActivity(intent)
        }

        buttonUnprocessedRequests.setOnClickListener {
            val intent = Intent(this, AdminUnprocessedRequestsActivity::class.java)

            startActivity(intent)
        }

        buttonProcessedDonations.setOnClickListener {
            val intent = Intent(this, AdminProcessedDonationsActivity::class.java)

            startActivity(intent)
        }

        buttonProcessedRequests.setOnClickListener {
            val intent = Intent(this, AdminProcessedRequestsActivity::class.java)

            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        initButtons()
    }
}