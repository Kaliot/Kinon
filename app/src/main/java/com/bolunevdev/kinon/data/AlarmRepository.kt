package com.bolunevdev.kinon.data

import com.bolunevdev.core_api.db.AlarmDao
import com.bolunevdev.core_api.entity.Alarm
import io.reactivex.rxjava3.core.Observable

class AlarmRepository(private val alarmDao: AlarmDao) {

    fun getAllFromDB(): Observable<List<Alarm>> = alarmDao.getAll()

    fun putToDb(alarm: Alarm) {
        alarmDao.insert(alarm)
    }

    fun deleteFromDB(alarm: Alarm) {
        alarmDao.delete(alarm)
    }
}