package com.bolunevdev.kinon.data

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.core.content.edit
import com.bolunevdev.kinon.R
import java.util.concurrent.TimeUnit

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
            preference.edit { putLong(KEY_FIRST_LAUNCH_DATE, System.currentTimeMillis()) }
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

    private fun isFreePeriodOver(): Boolean {
        val firstLaunchDate = preference.getLong(KEY_FIRST_LAUNCH_DATE, 0)
        val currentDate = System.currentTimeMillis()
        val daysPassed = TimeUnit.MILLISECONDS.toDays(currentDate - firstLaunchDate)
        return daysPassed > TRIAL_PERIOD_IN_DAYS
    }

    fun getNavigationGraphResId(): Int {
        return if (isFreePeriodOver()) {
            R.navigation.navigation_free
        } else {
            R.navigation.navigation
        }
    }

    fun getNavigationMenuResId(): Int {
        return if (isFreePeriodOver()) {
            R.menu.navigation_menu_free
        } else {
            R.menu.navigation_menu
        }
    }

    //Ключи для наших настроек, по ним мы их будем получать
    companion object {
        private const val KEY_SETTINGS_PREFERENCES = "settings"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_TIME_DB_UPDATE = "time_db_update"
        const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val DEFAULT_CATEGORY = "popular"
        private const val KEY_FIRST_LAUNCH_DATE = "first_launch_date"
        private const val TRIAL_PERIOD_IN_DAYS = 30
    }
}