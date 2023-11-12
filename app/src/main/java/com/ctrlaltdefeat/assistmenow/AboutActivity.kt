package com.ctrlaltdefeat.assistmenow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.ctrlaltdefeat.assistmenow.donors.DonorDashboardActivity
import com.ctrlaltdefeat.assistmenow.donors.DonorDropOffActivity
import com.ctrlaltdefeat.assistmenow.donors.DonorFinalizeActivity
import com.ctrlaltdefeat.assistmenow.objects.Donation

class AboutActivity : AppCompatActivity() {
    private lateinit var buttonBack: ImageView

    private fun initButtons() {
        buttonBack = findViewById(R.id.b_back)

        buttonBack.setOnClickListener {
            val intent = Intent(this, DonorDashboardActivity::class.java)

            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        initButtons()
    }
}