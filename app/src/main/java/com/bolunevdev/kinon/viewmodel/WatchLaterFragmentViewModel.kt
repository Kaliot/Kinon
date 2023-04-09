package com.bolunevdev.kinon.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bolunevdev.core_api.entity.Alarm
import com.bolunevdev.kinon.App
import com.bolunevdev.kinon.domain.Interactor
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class WatchLaterFragmentViewModel : ViewModel() {

    var alarmListObservable: Observable<List<Alarm>>

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        deleteOldAlarmsFromDB()
        alarmListObservable = interactor.getAlarmsFromDB()
        loadAlarmsList()
    }

    private fun loadAlarmsList() {
        alarmListObservable = interactor.getAlarmsFromDB()
    }

    private fun deleteOldAlarmsFromDB() {
        interactor.deleteOldAlarmsFromDB()
    }

    fun editAlarm(context: Context, alarm: Alarm) {
        interactor.editAlarm(context, alarm)
    }

    fun cancelAlarm(context: Context, alarm: Alarm) {
        interactor.cancelAlarm(context, alarm)
    }
}