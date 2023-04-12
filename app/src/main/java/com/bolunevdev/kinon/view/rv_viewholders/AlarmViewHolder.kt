package com.bolunevdev.kinon.view.rv_viewholders

import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bolunevdev.core_api.entity.Alarm
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.view.rv_adapters.AlarmListRecyclerAdapter
import com.bolunevdev.remote_module.ApiConstants
import com.bumptech.glide.Glide
import java.util.*

class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    //Привязываем View из layout к переменным
    private val title = itemView.findViewById<TextView>(R.id.title)
    private val poster = itemView.findViewById<ImageView>(R.id.poster)
    private val timeInMillis = itemView.findViewById<TextView>(R.id.dateTime)
    private val menuButton: ImageButton = itemView.findViewById(R.id.menuButton)

    //В этом методе кладем данные из Alarm в наши View
    fun bind(
        alarm: Alarm,
        clickListener: AlarmListRecyclerAdapter.OnMenuButtonClickListener,

        ) {
        //Устанавливаем заголовок
        title.text = alarm.title
        //Устанавливаем постер
        Glide.with(itemView)
            //Загружаем сам ресурс
            .load(ApiConstants.IMAGES_URL + IMAGE_SIZE + alarm.poster)
            .error(R.drawable.no_poster_holder)
            //Центруем изображение
            .centerCrop()
            //Указываем ImageView, куда будем загружать изображение
            .into(poster)
        //Устанавливаем дату и время
        val dateFormat = SimpleDateFormat("d MMMM HH:mm", Locale("ru"))
        timeInMillis.text = dateFormat.format(Date(alarm.timeInMillis))
        itemView.findViewById<View>(R.id.menuButton).setOnClickListener {
            clickListener.click(alarm, menuButton)
        }

    }

    companion object {
        private const val IMAGE_SIZE = "w342"
    }
}