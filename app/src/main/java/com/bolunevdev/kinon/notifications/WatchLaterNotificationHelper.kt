package com.bolunevdev.kinon.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.notifications.NotificationConstants.CHANNEL_ID
import com.bolunevdev.kinon.notifications.NotificationConstants.IMAGE_SIZE
import com.bolunevdev.kinon.view.activities.MainActivity.Companion.KEY_FILM_DETAILS_FRAGMENT
import com.bolunevdev.remote_module.ApiConstants
import com.bumptech.glide.Glide

class WatchLaterNotificationHelper(private val context: Context) {

    //Получаем доступ к менеджеру нотификаций
    private val notificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    init {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        //Создаем канал, передав в параметры его ID(строка), имя(строка), важность(константа)
        val mChannel = NotificationChannel(CHANNEL_ID, NAME, importance)
        //Отдельно задаем описание
        mChannel.description = DESCRIPTION_TEXT
        //Регистрируем канал
        notificationManager.createNotificationChannel(mChannel)
    }

    fun sendWatchLaterNotification(film: Film) {
        val pendingIntent = createPendingIntent(film)
        val notification = createNotification(film, pendingIntent)
        notificationManager.notify(film.filmId, notification)
    }

    private fun createPendingIntent(film: Film) = NavDeepLinkBuilder(context)
        .setGraph(R.navigation.navigation)
        .setDestination(R.id.detailsFragmentHome)
        .setArguments(bundleOf(KEY_FILM_DETAILS_FRAGMENT to film))
        .createPendingIntent()

    private fun createNotification(film: Film, pendingIntent: PendingIntent): Notification {
        val action = Notification.Action.Builder(
            null,
            context.getString(R.string.notification_action_watch),
            pendingIntent
        ).build()

        val notificationBuilder = Notification.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(film.title)
            .setAutoCancel(true)
            .addAction(action)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notification)

        film.poster?.let {
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(ApiConstants.IMAGES_URL + IMAGE_SIZE + it)
                .submit()
                .get()
            notificationBuilder.setStyle(Notification.BigPictureStyle().bigPicture(bitmap))
        }

        return notificationBuilder.build()
    }

    companion object {
        private const val NAME = "KINON_WATCH_LATER_NOTIFICATION"
        private const val DESCRIPTION_TEXT = "Kinon Notification Channel"
    }
}