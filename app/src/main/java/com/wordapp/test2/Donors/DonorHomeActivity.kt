package com.wordapp.test2.Donors

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.wordapp.test2.AboutActivity
import com.wordapp.test2.Admin.AdminCompletedDon
import com.wordapp.test2.Admin.AdminCompletedReq
import com.wordapp.test2.Admin.AdminOngoingDon
import com.wordapp.test2.Admin.AdminOngoingReq
import com.wordapp.test2.R

class DonorHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_home)
    }
    fun openOngoingDonationsActivity(view: View) {
        val intent = Intent(this, OngoingDonationsActivity::class.java)
        startActivity(intent)
    }

    fun openDonorActivity(view: View) {
        val intent = Intent(this, DonorActivity::class.java)
        startActivity(intent)
    }

    fun openAboutActivity(view: View) {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }


}
