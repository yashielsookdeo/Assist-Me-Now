package com.ctrlaltdefeat.assistmenow

import android.app.Application
import com.ctrlaltdefeat.assistmenow.database.Firebase

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Firebase.init(this)
    }
}