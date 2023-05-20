package com.bolunevdev.kinon.data

import com.bolunevdev.core_api.db.AlarmDao
import com.bolunevdev.core_api.entity.Alarm
import com.bolunevdev.kinon.domain.AlarmRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(private val alarmDao: AlarmDao) : AlarmRepository {

    override fun getAllFromDB(): Observable<List<Alarm>> = alarmDao.getAll()

    override fun putToDb(alarm: Alarm) {
        alarmDao.insert(alarm)
    }

    override fun deleteFromDB(alarm: Alarm) {
        alarmDao.delete(alarm)
    }
}