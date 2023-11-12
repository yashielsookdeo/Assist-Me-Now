package com.ctrlaltdefeat.assistmenow.recipients

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ctrlaltdefeat.assistmenow.AboutActivity
import com.ctrlaltdefeat.assistmenow.R
import com.ctrlaltdefeat.assistmenow.donors.DonorDonationsActivity
import com.ctrlaltdefeat.assistmenow.donors.DonorMakeActivity

class RecipientDashboardActivity : AppCompatActivity() {
    private lateinit var buttonCreateRequest: Button
    private lateinit var buttonRequests: Button
    private lateinit var buttonAbout: Button
    private lateinit var buttonDropOffs: Button

    private fun initButtons() {
        buttonCreateRequest = findViewById(R.id.b_requestdonation)
        buttonRequests = findViewById(R.id.b_requests)
        buttonAbout = findViewById(R.id.b_about)
        buttonDropOffs = findViewById(R.id.b_dropoffs)

        buttonCreateRequest.setOnClickListener {
            val intent = Intent(this, RecipientRequestActivity::class.java)

            startActivity(intent)
        }

        buttonRequests.setOnClickListener {
            val intent = Intent(this, RecipientRequestsActivity::class.java)

            startActivity(intent)
        }

        buttonAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)

            startActivity(intent)
        }

        buttonDropOffs.setOnClickListener {
            val intent = Intent(this, RecipientViewDropOffsActivity::class.java)

            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient_dashboard)

        initButtons()
    }
}