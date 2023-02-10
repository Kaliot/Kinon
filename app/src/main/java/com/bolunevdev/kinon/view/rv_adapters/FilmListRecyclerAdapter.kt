package com.bolunevdev.kinon.view.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.utils.FilmDiff
import com.bolunevdev.kinon.view.rv_viewholders.FilmViewHolder

//в параметр передаем слушатель, чтобы мы потом могли обрабатывать нажатия из класса Activity
class FilmListRecyclerAdapter(
    private val clickListener: OnItemClickListener,
    private val longClickListener: OnItemLongClickListener,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<Film>()

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
                    items[position].id.toString()
                )
                //Вызываем метод bind(), который мы создали, и передаем туда объект
                //из нашей базы данных с указанием позиции
                holder.bind(items[position], clickListener, longClickListener)
            }
        }
    }

    fun updateData(filmsDataBase: List<Film>) {
        val oldFilmsList = items
        //Кладем нашу БД в RV
        val diff = FilmDiff(oldFilmsList, filmsDataBase)
        val diffResult = DiffUtil.calculateDiff(diff)
        items = filmsDataBase
        diffResult.dispatchUpdatesTo(this)
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

