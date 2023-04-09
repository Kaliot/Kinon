package com.bolunevdev.kinon.utils

import android.app.Activity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.view.activities.MainActivity
import java.util.concurrent.Executors
import kotlin.math.hypot
import kotlin.math.roundToInt

object AnimationHelper {
    //Это переменная для того, что бы круг проявления расходился именно от иконки меню навигации
    private const val MENU_ITEMS_NUM = 5

    //В метод у нас приходит 3 параметра:
    //1 - наше rootView, которое одновременно является и контейнером
    //и объектом анимации
    //2 - активити, для того чтобы вернуть выполнение нового треда в UI поток
    //3 - позиция в меню навигации, что бы круг проявления расходился именно от иконки меню навигации
    fun performFragmentCircularRevealAnimation(rootView: View, activity: Activity, position: Int) {
        //Создаем новый тред
        Executors.newSingleThreadExecutor().execute {
            //В бесконечном цикле проверям, когда наше анимируемое view будет "прикреплено" к экрану
            while (true) {
                //Когда оно будет прикреплено выполним код
                if (rootView.isAttachedToWindow && rootView.visibility == View.INVISIBLE) {
                    //Возвращаемся в главный тред, чтобы выполнить анимацию
                    activity.runOnUiThread {
                        activity.findViewById<FrameLayout>(R.id.backgroundShareTransition).visibility =
                            View.GONE
                        //Cупер сложная математика вычесления старта анимации
                        val centerMultiplier = 2
                        val itemCenter = rootView.width / (MENU_ITEMS_NUM * centerMultiplier)
                        val step = (itemCenter * centerMultiplier) * position - itemCenter

                        val x: Int = step
                        val y: Int = rootView.y.roundToInt() + rootView.height

                        val startRadius = 0
                        val endRadius = hypot(rootView.width.toDouble(), rootView.height.toDouble())
                        //Создаем саму анимацию
                        ViewAnimationUtils.createCircularReveal(
                            rootView, x, y, startRadius.toFloat(), endRadius.toFloat()
                        ).apply {
                            //Устанавливаем время анимации
                            duration = MainActivity.TRANSITION_DURATION
                            //Интерполятор для более естесственной анимации
                            interpolator = AccelerateDecelerateInterpolator()
                            //Запускаем
                            start()
                        }
                        //Выставляяем видимость нашего елемента
                        rootView.visibility = View.VISIBLE
                    }
                    return@execute
                } else if (rootView.isAttachedToWindow && rootView.visibility == View.VISIBLE) return@execute
            }
        }
    }
}