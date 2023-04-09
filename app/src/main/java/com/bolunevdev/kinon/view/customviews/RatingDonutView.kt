package com.bolunevdev.kinon.view.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.view.rv_viewholders.FilmViewHolder.Companion.RATING_MULTIPLIER


class RatingDonutView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : View(context, attributeSet) {

    private val backgroundBitmap: Bitmap by lazy {
        Bitmap.createBitmap(
            (centerX * 2).toInt(),
            (centerY * 2).toInt(),
            Bitmap.Config.ARGB_8888
        )
    }
    private val backgroundStaticCanvas: Canvas by lazy {
        Canvas(backgroundBitmap)
    }

    private var isStaticBackgroundDrawn: Boolean = false

    //Овал для рисования сегментов прогресс бара
    private val oval = RectF()

    //Координаты центра View, а также Radius
    private var radius: Float = 0f
    private var centerX: Float = 0f
    private var centerY: Float = 0f

    //Толщина линии прогресса
    private var stroke = 10f

    //Значение прогресса от 0 - 100
    private var progress = 50

    //Значения размера текста внутри кольца
    private var scaleSize = 60f

    //Краски для наших фигур
    private var strokePaint: Paint? = null
    private var digitPaint: Paint? = null
    private var circlePaint: Paint? = null

    private var isFirstRun: Boolean = true

    init {
        //Получаем атрибуты и устанавливаем их в соответствующие поля
        val a =
            context.theme.obtainStyledAttributes(attributeSet, R.styleable.RatingDonutView, 0, 0)
        try {
            stroke = a.getFloat(
                R.styleable.RatingDonutView_stroke, stroke
            )
            progress = a.getInt(R.styleable.RatingDonutView_progress, progress)
        } finally {
            a.recycle()
        }
        //Инициализируем первоначальные краски
        initPaint()
    }

    private fun initPaint() {
        //Краска для колец
        strokePaint = Paint().apply {
            style = Paint.Style.STROKE
            //Сюда кладем значение из поля класса, потому как у нас краски будут видоизменяться
            strokeWidth = stroke
            //Цвет мы тоже будем получать в специальном методе, потому что в зависимости от рейтинга
            //мы будем менять цвет нашего кольца
            color = getPaintColor(progress)
            isAntiAlias = true
        }
        //Краска для цифр
        val shadowRadius = 5f
        digitPaint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 2f
            setShadowLayer(shadowRadius, 0f, 0f, Color.DKGRAY)
            textSize = scaleSize
            typeface = Typeface.SANS_SERIF
            color = getPaintColor(progress)
            isAntiAlias = true
        }
        //Краска для заднего фона
        circlePaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.DKGRAY
            alpha = ALPHA
        }
    }

    private fun getPaintColor(progress: Int): Int = when (progress) {
        in 0..25 -> Color.argb(
            ALPHA_VALUE,
            START_RED + RED_INCREMENT * progress / MIN_FACTOR,
            0,
            0
        )
        in 26..50 -> Color.argb(
            ALPHA_VALUE,
            MAX_RED,
            START_GREEN * (progress - MIN_FACTOR) / MIN_FACTOR,
            0
        )
        in 51..75 -> Color.argb(
            ALPHA_VALUE,
            MAX_RED - RED_INCREMENT * (progress - MID_FACTOR) / MIN_FACTOR,
            START_GREEN + GREEN_INCREMENT * (progress - MID_FACTOR) / MIN_FACTOR,
            0
        )
        else -> Color.argb(
            ALPHA_VALUE,
            START_RED - START_RED * (progress - MAX_FACTOR) / MIN_FACTOR,
            MAX_GREEN,
            0
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = if (width > height) {
            height.div(2f)
        } else {
            width.div(2f)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val chosenWidth = chooseDimension(widthMode, widthSize)
        val chosenHeight = chooseDimension(heightMode, heightSize)

        val minSide = chosenWidth.coerceAtMost(chosenHeight)
        centerX = minSide.div(2f)
        centerY = minSide.div(2f)
        String.format("%.1f", progress / 10f)

        setMeasuredDimension(minSide, minSide)
    }

    private fun chooseDimension(mode: Int, size: Int) =
        when (mode) {
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> size
            else -> DEFAULT_DIMENSION
        }

    private fun drawRating(canvas: Canvas) {
        //Здесь мы можем регулировать размер нашего кольца
        val scale = radius * SCALE_COEFFICIENT
        //Сохраняем канвас
        canvas.save()
        //Перемещаем нулевые координаты канваса в центр, вы помните, так проще рисовать все круглое
        canvas.translate(centerX, centerY)
        //Устанавливаем размеры под наш овал
        oval.set(0f - scale, 0f - scale, scale, scale)
        //Рисуем "арки", из них и будет состоять наше кольцо + у нас тут специальный метод
        strokePaint?.let {
            canvas.drawArc(
                oval, -90f, convertProgressToDegrees(progress), false,
                it
            )
        }
        //Восстанавливаем канвас
        canvas.restore()
    }

    private fun convertProgressToDegrees(progress: Int): Float = progress * 3.6f

    private fun drawText(canvas: Canvas) {
        //Форматируем текст, чтобы мы выводили дробное число с одной цифрой после точки
        val message = String.format("%.1f", progress / RATING_MULTIPLIER)
        //Получаем ширину и высоту текста, чтобы компенсировать их при отрисовке, чтобы текст был
        //точно в центре
        val widths = FloatArray(message.length)
        digitPaint?.getTextWidths(message, widths)
        var advance = 0f
        for (width in widths) advance += width
        //Рисуем наш текст
        digitPaint?.let {
            canvas.drawText(
                message, centerX - advance / 2, centerY + advance / 4,
                it
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        //Рисуем задний фон
        if (!isStaticBackgroundDrawn) {
            drawStaticBackground()
        }
        canvas.drawBitmap(backgroundBitmap, centerX - radius, centerY - radius, null)
        //Рисуем кольцо
        drawRating(canvas)
        //Рисуем цифры
        drawText(canvas)
    }

    private fun drawStaticBackground() {
        drawBackground(backgroundStaticCanvas)

        isStaticBackgroundDrawn = true
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.save()

        canvas.translate(centerX, centerY)

        circlePaint?.let { canvas.drawCircle(0f, 0f, radius, it) }
    }

    fun setProgress(pr: Int) {
        val animator = ValueAnimator.ofInt(0, pr)
        if (isFirstRun) {
            animator.duration = ANIMATION_DURATION
            animator.addUpdateListener {
                //Кладем новое значение в наше поле класса
                progress = it.animatedValue as Int
                //Создаем краски с новыми цветами
                initPaint()
                //вызываем перерисовку View
                invalidate()
            }
            animator.start()
            isFirstRun = false
        } else {
            progress = pr
            initPaint()
            invalidate()
        }
    }

    companion object {
        const val ANIMATION_DURATION = 600L
        const val SCALE_COEFFICIENT = 0.8f
        const val DEFAULT_DIMENSION = 300
        const val ALPHA = 200
        const val ALPHA_VALUE = 255
        const val MAX_FACTOR = 75
        const val MIN_FACTOR = 25
        const val MID_FACTOR = 50
        const val MAX_RED = 255
        const val START_RED = 170
        const val RED_INCREMENT = 85
        const val START_GREEN = 170
        const val GREEN_INCREMENT = 85
        const val MAX_GREEN = 255
    }
}