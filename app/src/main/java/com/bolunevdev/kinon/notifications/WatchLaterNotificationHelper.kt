package com.bolunevdev.kinon.notifications

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.notifications.NotificationConstants.ALARM_BUNDLE_KEY
import com.bolunevdev.kinon.notifications.NotificationConstants.CHANNEL_ID
import com.bolunevdev.kinon.notifications.NotificationConstants.IMAGE_SIZE
import com.bolunevdev.kinon.notifications.NotificationConstants.NOTIFICATION_INTENT_KEY
import com.bolunevdev.kinon.receivers.WatchLaterNotificationReceiver
import com.bolunevdev.kinon.view.activities.MainActivity.Companion.KEY_FILM_DETAILS_FRAGMENT
import com.bolunevdev.remote_module.ApiConstants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

object WatchLaterNotificationHelper {

    //Получаем доступ к менеджеру нотификаций
    private var notificationManager: NotificationManager? = null
    private const val NAME = "KINON_WATCH_LATER_NOTIFICATION"
    private const val DESCRIPTION_TEXT = "Kinon Notification Channel"

    // Функция для установки будильника
    fun setWatchLaterAlarm(
        context: Context?,
        film: Film,
        callback: ((alarmTime: Long) -> Unit)?
    ) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(film.title, null, context, WatchLaterNotificationReceiver()::class.java)
        val bundle = Bundle()
        bundle.putParcelable(KEY_FILM_DETAILS_FRAGMENT, film)
        val notificationIntent = createPendingIntent(context, film)
        intent.putExtra(NOTIFICATION_INTENT_KEY, notificationIntent)
        intent.putExtra(ALARM_BUNDLE_KEY, bundle)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        // Установка будильника с помощью AlarmManager
                        alarmManager.set(AlarmManager.RTC, calendar.timeInMillis, pendingIntent)
                        // Вызов обратного вызова, передающего время будильника
                        callback?.invoke(calendar.timeInMillis)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun cancelWatchLaterAlarm(context: Context?, film: Film) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(film.title, null, context, WatchLaterNotificationReceiver()::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }

    fun sendWatchLaterNotification(context: Context, film: Film, pendingIntent: PendingIntent) {

        notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        //Создаем канал, передав в параметры его ID(строка), имя(строка), важность(константа)
        val mChannel = NotificationChannel(CHANNEL_ID, NAME, importance)
        //Отдельно задаем описание
        mChannel.description = DESCRIPTION_TEXT
        mChannel.importance = NotificationManager.IMPORTANCE_DEFAULT
        //Регистрируем канал
        notificationManager?.createNotificationChannel(mChannel)
        createNotification(context, film, pendingIntent)
    }

    private fun createPendingIntent(context: Context, film: Film) = NavDeepLinkBuilder(context)
        .setGraph(R.navigation.navigation)
        .setDestination(R.id.detailsFragmentHome)
        .setArguments(bundleOf(KEY_FILM_DETAILS_FRAGMENT to film))
        .createPendingIntent()

    private fun createNotification(context: Context, film: Film, pendingIntent: PendingIntent) {
        val notificationBuilder = Notification.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(film.title)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)

        Glide.with(context)
            //говорим что нужен битмап
            .asBitmap()
            //указываем откуда загружать, это ссылка как на загрузку с API
            .load(ApiConstants.IMAGES_URL + IMAGE_SIZE + film.poster)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                //Этот коллбэк отработает когда мы успешно получим битмап
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    //Создаем нотификации в стиле big picture
                    notificationBuilder.style = Notification.BigPictureStyle().bigPicture(resource)
                    notificationManager?.notify(film.id, notificationBuilder.build())
                }
            })
        notificationManager?.notify(film.id, notificationBuilder.build())

    }
}