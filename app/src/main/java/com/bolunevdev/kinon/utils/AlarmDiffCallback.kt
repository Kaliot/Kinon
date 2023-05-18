package com.bolunevdev.kinon.utils

import androidx.recyclerview.widget.DiffUtil
import com.bolunevdev.core_api.entity.Alarm

class AlarmDiffCallback : DiffUtil.ItemCallback<Alarm>() {

    override fun areItemsTheSame(oldAlarm: Alarm, newAlarm: Alarm): Boolean {
        return oldAlarm == newAlarm
    }

    override fun areContentsTheSame(oldAlarm: Alarm, newAlarm: Alarm): Boolean {
        return oldAlarm == newAlarm
    }
}