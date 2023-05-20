package com.bolunevdev.kinon.data

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.core.content.edit

class PreferenceProvider(context: Context) {
    //Нам нужен контекст приложения
    private val appContext = context.applicationContext

    //Создаем экземпляр SharedPreferences
    private val preference: SharedPreferences =
        appContext.getSharedPreferences(KEY_SETTINGS_PREFERENCES, Context.MODE_PRIVATE)

    init {
        //Логика для первого запуска приложения, чтобы положить наши настройки,
        //Сюда потом можно добавить и другие настройки
        if (preference.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preference.edit { putString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) }
            preference.edit { putBoolean(KEY_FIRST_LAUNCH, false) }
        }
    }

    //Category prefs
    //Сохраняем категорию
    fun saveDefaultCategory(category: String) {
        preference.edit { putString(KEY_DEFAULT_CATEGORY, category) }
    }

    //Забираем категорию
    fun getDefaultCategory(): String {
        return preference.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY
    }


    //Добавляем слушателя категорий из настроек
    fun addSharedPreferencesCategoryChangeListener(listener: OnSharedPreferenceChangeListener) {
        preference.registerOnSharedPreferenceChangeListener(listener)
    }

    //Удаляем слушателя категорий из настроек
    fun unregisterSharedPreferencesCategoryChangeListener(listener: OnSharedPreferenceChangeListener) {
        preference.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun setTimeOfDatabaseUpdate(time: Long) {
        preference.edit { putLong(KEY_TIME_DB_UPDATE, time) }
    }

    fun getTimeOfDatabaseUpdate(): Long {
        return preference.getLong(KEY_TIME_DB_UPDATE, 0)
    }

    //Ключи для наших настроек, по ним мы их будем получать
    companion object {
        private const val KEY_SETTINGS_PREFERENCES = "settings"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_TIME_DB_UPDATE = "time_db_update"
        const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val DEFAULT_CATEGORY = "popular"
    }
}