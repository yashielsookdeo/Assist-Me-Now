package com.ctrlaltdefeat.assistmenow.donors

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.ctrlaltdefeat.assistmenow.AboutActivity
import com.ctrlaltdefeat.assistmenow.R

class DonorDashboardActivity : AppCompatActivity() {
    private lateinit var buttonCreateDonation: Button
    private lateinit var buttonDonations: Button
    private lateinit var buttonAbout: Button
    private lateinit var buttonPickUps: Button

    private fun initButtons() {
        buttonCreateDonation = findViewById(R.id.b_createdonation)
        buttonDonations = findViewById(R.id.b_donations)
        buttonAbout = findViewById(R.id.b_about)
        buttonPickUps = findViewById(R.id.b_pickups)

        buttonCreateDonation.setOnClickListener {
            val intent = Intent(this, DonorMakeActivity::class.java)

            startActivity(intent)
        }

        buttonDonations.setOnClickListener {
            val intent = Intent(this, DonorDonationsActivity::class.java)

            startActivity(intent)
        }

        buttonAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)

            startActivity(intent)
        }

        buttonPickUps.setOnClickListener {
            val intent = Intent(this, DonorViewPickUpsActivity::class.java)

            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_dashboard)

        initButtons()
    }
}