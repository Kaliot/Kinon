package com.bolunevdev.kinon.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.notifications.NotificationConstants.ALARM_BUNDLE_KEY
import com.bolunevdev.kinon.notifications.NotificationConstants.NOTIFICATION_INTENT_KEY
import com.bolunevdev.kinon.notifications.WatchLaterNotificationHelper
import com.bolunevdev.kinon.view.activities.MainActivity.Companion.KEY_FILM_DETAILS_FRAGMENT

class WatchLaterNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val bundle = intent.getBundleExtra(ALARM_BUNDLE_KEY)
        val film: Film
        val notificationIntent: PendingIntent

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            film = bundle?.getParcelable(KEY_FILM_DETAILS_FRAGMENT, Film::class.java) as Film
            notificationIntent = intent.getParcelableExtra(
                NOTIFICATION_INTENT_KEY,
                PendingIntent::class.java
            ) as PendingIntent

        } else {
            @Suppress("DEPRECATION")
            film = bundle?.getParcelable<Film>(KEY_FILM_DETAILS_FRAGMENT) as Film
            @Suppress("DEPRECATION")
            notificationIntent =
                intent.getParcelableExtra<PendingIntent>(NOTIFICATION_INTENT_KEY) as PendingIntent
        }
        WatchLaterNotificationHelper.sendWatchLaterNotification(
            context,
            film,
            notificationIntent
        )
    }
}