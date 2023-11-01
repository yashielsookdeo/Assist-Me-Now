package com.wordapp.test2.Admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.wordapp.test2.R

class AdminHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)
    }

    fun openAdminOngoingReq(view: View) {
        val intent = Intent(this, AdminOngoingReq::class.java)
        startActivity(intent)
    }

    fun openAdminOngoingDon(view: View) {
        val intent = Intent(this, AdminOngoingDon::class.java)
        startActivity(intent)
    }

    fun openAdminCompletedReq(view: View) {
        val intent = Intent(this, AdminCompletedReq::class.java)
        startActivity(intent)
    }

    fun openAdminCompletedDon(view: View) {
        val intent = Intent(this, AdminCompletedDon::class.java)
        startActivity(intent)
    }
}
