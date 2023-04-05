package com.bolunevdev.kinon.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class BatteryBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BATTERY_LOW -> {
                switchToNightTheme()
                Toast.makeText(context, BATTERY_LOW_TOAST, Toast.LENGTH_SHORT).show()
            }
            Intent.ACTION_POWER_CONNECTED -> {
                switchToDefaultTheme()
                Toast.makeText(context, POWER_CONNECTED_TOAST, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun switchToNightTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun switchToDefaultTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    companion object {
        private const val BATTERY_LOW_TOAST = "Низкий заряд батареи!"
        private const val POWER_CONNECTED_TOAST = "Зарядное устройство подключено"
    }
}