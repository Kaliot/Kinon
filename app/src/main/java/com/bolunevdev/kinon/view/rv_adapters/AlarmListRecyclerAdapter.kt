package com.bolunevdev.kinon.view.rv_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bolunevdev.core_api.entity.Alarm
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.utils.AlarmDiff
import com.bolunevdev.kinon.view.rv_viewholders.AlarmViewHolder

class AlarmListRecyclerAdapter(
    private val clickListener: OnMenuButtonClickListener,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<Alarm>()

    //Этот метод нужно переопределить на возврат количества элементов в списке RV
    override fun getItemCount() = items.size

    //В этом методе мы привязываем наш ViewHolder и передаем туда "надутую" верстку нашего фильма
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AlarmViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
        )
    }

    //В этом методе будет привязка полей из объекта Alarm к View из alarm_item.xml
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AlarmViewHolder -> {
                //Вызываем метод bind(), который мы создали, и передаем туда объект
                //из нашей базы данных с указанием позиции
                holder.bind(items[position], clickListener)
            }
        }
    }

    fun updateData(filmsDataBase: List<Alarm>) {
        val oldAlarmsList = items
        //Кладем нашу БД в RV
        val diff = AlarmDiff(oldAlarmsList, filmsDataBase)
        val diffResult = DiffUtil.calculateDiff(diff)
        items = filmsDataBase
        diffResult.dispatchUpdatesTo(this)
    }

    //Интерфейс для обработки кликов
    interface OnMenuButtonClickListener {
        fun click(alarm: Alarm, menuButton: View)
    }
}