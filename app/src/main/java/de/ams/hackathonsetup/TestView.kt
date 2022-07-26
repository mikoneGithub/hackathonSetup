package de.ams.hackathonsetup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Integer.min
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.random.Random

class TestView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr), CoroutineScope {

    private val colors = arrayOf(
        Color.BLUE,
        Color.CYAN,
        Color.RED,
        Color.YELLOW,
        Color.MAGENTA,
        Color.GRAY,
        Color.GREEN
    )

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = colors[0]
    }

    private val updateJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + updateJob

    private val rectSize =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics)
            .toInt()
    private val speed = 5

    private val rect = Rect()
    private var currentX = 0
    private var currentY = 0
    private var directionX = true
    private var directionY = true
    private var counter = 0

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        launch {
            while (true) {
                invalidate()
                delay(30L)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        updateJob.cancel()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        calcNewRect()
        canvas.drawRect(rect, paint)
    }

    private fun calcNewRect() {
        currentX = if (directionX) {
            currentX += speed
            val tmpDirection = currentX + rectSize <= width
            checkDirectionChange(directionX, tmpDirection)
            directionX = tmpDirection
            min(width - rectSize, currentX)
        } else {
            currentX -= speed
            val tmpDirection = currentX <= 0
            checkDirectionChange(directionX, tmpDirection)
            directionX = tmpDirection
            max(currentX, 0)
        }

        currentY = if (directionY) {
            currentY += speed
            val tmpDirection = currentY + rectSize <= height
            checkDirectionChange(directionY, tmpDirection)
            directionY = tmpDirection
            min(height - rectSize, currentY)
        } else {
            currentY -= speed
            val tmpDirection = currentY <= 0
            checkDirectionChange(directionY, tmpDirection)
            directionY = tmpDirection
            max(currentY, 0)
        }

        rect.set(currentX, currentY, currentX + rectSize, currentY + rectSize)
    }

    private fun checkDirectionChange(oldDirection: Boolean, newDirection: Boolean) {
        if (oldDirection != newDirection) {
            paint.color = colors[++counter]

            if (counter == colors.size - 1) {
                counter = -1
            }
        }
    }
}