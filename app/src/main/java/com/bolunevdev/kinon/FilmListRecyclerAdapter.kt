package com.bolunevdev.kinon

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

//в параметр передаем слушатель, чтобы мы потом могли обрабатывать нажатия из класса Activity
class FilmListRecyclerAdapter(
    private val clickListener: OnItemClickListener,
    private val longClickListener: OnItemLongClickListener,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //Здесь у нас хранится список элементов для RV
    var items = mutableListOf<Film>()
        set(newValue) {
            val diffCallback = FilmDiff(field as ArrayList<Film>, newValue as ArrayList<Film>)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    //Этот метод нужно переопределить на возврат количества элементов в списке RV
    override fun getItemCount() = items.size

    //В этом методе мы привязываем наш ViewHolder и передаем туда "надутую" верстку нашего фильма
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FilmViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.film_item, parent, false)
        )
    }

    //В этом методе будет привязка полей из объекта Film к View из film_item.xml
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FilmViewHolder -> {
                ViewCompat.setTransitionName(
                    holder.itemView.findViewById(R.id.poster),
                    items[position].poster.toString()
                )
                //Вызываем метод bind(), который мы создали, и передаем туда объект
                //из нашей базы данных с указанием позиции
                holder.bind(items[position], clickListener, longClickListener)
            }
        }
    }

    //Интерфейс для обработки кликов
    interface OnItemClickListener {
        fun click(film: Film, poster: ImageView)
    }

    //Интерфейс для обработки длительного нажатия
    interface OnItemLongClickListener {
        fun longClick(film: Film)
    }
}

