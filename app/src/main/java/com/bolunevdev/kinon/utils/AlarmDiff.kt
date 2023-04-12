package com.bolunevdev.kinon.utils

import androidx.recyclerview.widget.DiffUtil
import com.bolunevdev.core_api.entity.Alarm

class AlarmDiff(private val oldList: List<Alarm>, private val newList: List<Alarm>) :
    DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldAlarm = oldList[oldItemPosition]
        val newAlarm = newList[newItemPosition]
        return oldAlarm == newAlarm
    }
}