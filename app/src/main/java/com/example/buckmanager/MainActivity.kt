package com.example.buckmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.buckmanager.ui.BuckApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.buckmanager.model.CurrencyConfig.load(this)
        enableEdgeToEdge()

        val syncRequest = androidx.work.PeriodicWorkRequestBuilder<com.example.buckmanager.data.DriveSyncWorker>(
            24, java.util.concurrent.TimeUnit.HOURS
        ).build()
        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DriveSync",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        setContent {
            BuckApp()
        }
    }
}
