package com.bolunevdev.kinon.domain

import com.bolunevdev.core_api.entity.Alarm
import io.reactivex.rxjava3.core.Observable

interface AlarmRepository {

    fun getAllFromDB(): Observable<List<Alarm>>

    fun putToDb(alarm: Alarm)

    fun deleteFromDB(alarm: Alarm)
}