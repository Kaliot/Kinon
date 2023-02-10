package com.bolunevdev.kinon.view.rv_viewholders


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionSet
import com.bolunevdev.kinon.view.rv_adapters.FilmListRecyclerAdapter
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.data.ApiConstants
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.view.activities.MainActivity
import com.bolunevdev.kinon.view.customviews.RatingDonutView
import com.bumptech.glide.Glide

class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    //Привязываем View из layout к переменным
    private val title = itemView.findViewById<TextView>(R.id.title)
    private val poster = itemView.findViewById<ImageView>(R.id.poster)
    private val description = itemView.findViewById<TextView>(R.id.description)
    private val ratingDonut = itemView.findViewById<RatingDonutView>(R.id.rating_donut)

    //В этом методе кладем данные из Film в наши View
    fun bind(
        film: Film,
        clickListener: FilmListRecyclerAdapter.OnItemClickListener,
        longClickListener: FilmListRecyclerAdapter.OnItemLongClickListener
    ) {
        //Устанавливаем заголовок
        title.text = film.title
        //Устанавливаем постер
        Glide.with(itemView)
            //Загружаем сам ресурс
            .load(ApiConstants.IMAGES_URL + IMAGE_SIZE + film.poster)
            .error(R.drawable.no_poster_holder)
            //Центруем изображение
            .centerCrop()
            //Указываем ImageView, куда будем загружать изображение
            .into(poster)
        //Устанавливаем описание
        description.text = film.description
        //Устанавливаем рейтинг
        ratingDonut.setProgress((film.rating * RATING_MULTIPLIER).toInt())
        //Обрабатываем нажатие на весь элемент целиком(можно сделать на отдельный элемент
        //например, картинку) и вызываем метод нашего листенера, который мы получаем из
        //конструктора адаптера
        itemView.findViewById<View>(R.id.item_container).setOnClickListener {
            it.findFragment<Fragment>().exitTransition = TransitionSet().apply {
                addTransition(
                    Fade(Fade.OUT)
                        .setDuration(MainActivity.TRANSITION_DURATION)
                        .excludeTarget(it, true)
                )
                addTransition(
                    Fade(Fade.OUT)
                        .addTarget(it)
                        .setDuration(MainActivity.TRANSITION_DURATION_FAST)
                )
            }
            clickListener.click(film, poster)
        }

        itemView.findViewById<View>(R.id.item_container).setOnLongClickListener {
            longClickListener.longClick(film)
            return@setOnLongClickListener true
        }
    }

    companion object {
        const val RATING_MULTIPLIER = 10f
        private const val IMAGE_SIZE = "w342"
    }
}