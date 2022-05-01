package com.example.themetronomeplaylist.views

import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView.ScaleType
import android.widget.RelativeLayout
import androidx.core.view.GestureDetectorCompat
import com.example.themetronomeplaylist.R
import kotlinx.android.synthetic.main.rotary_knob_view.view.*
import kotlin.math.atan2


class RotaryKnobView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), GestureDetector.OnGestureListener {
    private val gestureDetector: GestureDetectorCompat
    private var maxValue = 99
    private var minValue = 0
    var listener: RotaryKnobListener? = null
    var value = 130
    private var knobDrawable: Drawable? = null
    private var divider = 300f / (maxValue - minValue) //scale the results to the passed range

    interface RotaryKnobListener {
        fun onRotate(value: Int)
    }

    init {
        this.maxValue = maxValue + 1 // To allow reaching last defined value

        LayoutInflater.from(context)
            .inflate(R.layout.rotary_knob_view, this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RotaryKnobView,
            0,
            0
        ).apply {
            try {
                minValue = getInt(R.styleable.RotaryKnobView_minValue, 40)
                maxValue = getInt(R.styleable.RotaryKnobView_maxValue, 220) + 1
                value = getInt(R.styleable.RotaryKnobView_initialValue, 130)
                knobDrawable = getDrawable(R.styleable.RotaryKnobView_knobDrawable)
                divider = 300f / (maxValue - minValue)
                knobImageView.setImageDrawable(knobDrawable)
            } finally {
                recycle()
            }
        }
        gestureDetector = GestureDetectorCompat(context, this)
    }

    private fun calculateAngle(x: Float, y: Float): Float {
        val px = (x / width.toFloat()) - 0.5
        val py = ( 1 - y / height.toFloat()) - 0.5
        var angle = -(Math.toDegrees(atan2(py, px)))
            .toFloat() + 90
        if (angle > 180) angle -= 360
        return angle
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event))
            true
        else
            super.onTouchEvent(event)
    }

    private fun setKnobPosition(deg: Float) {
        // to allow setting the knob position before onMeasure or onLayout ran
        val x = if (width != 0) {
            width.toFloat() / 2
        } else {
            resources.getDimension(R.dimen.knob_width) / 2
        }

        val y = if (height != 0) {
            height.toFloat() / 2
        } else {
            resources.getDimension(R.dimen.knob_height) / 2
        }

        val matrix = Matrix()
        knobImageView.scaleType = ScaleType.MATRIX
        matrix.postRotate(deg, x, y)
        knobImageView.imageMatrix = matrix
    }

    fun setKnobPositionByValue(value: Int) {
        var angle = ((value - minValue) * divider) -150
        if (angle > 180) angle -= 360
        Log.i("KNOB", "seet position to $angle")
        setKnobPosition(angle)
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float)
            : Boolean {

        val rotationDegrees = calculateAngle(e2.x, e2.y)
        // use only -150 to 150 range (knob min/max points
        if (rotationDegrees >= -150 && rotationDegrees <= 150) {
            setKnobPosition(rotationDegrees)

            // Calculate rotary value
            // The range is the 300 degrees between -150 and 150, so we'll add 150 to adjust the
            // range to 0 - 300
            val valueRangeDegrees = rotationDegrees + 150
            value = ((valueRangeDegrees / divider) + minValue).toInt()
            if (listener != null) listener!!.onRotate(value)
        }
        return true
    }

    // Unused. Needed for GestureDetector implementation
    override fun onDown(event: MotionEvent): Boolean {
        return true
    }
    // Unused. Needed for GestureDetector implementation
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return true
    }

    // Unused. Needed for GestureDetector implementation
    override fun onFling(arg0: MotionEvent, arg1: MotionEvent, arg2: Float, arg3: Float)
            : Boolean {
        return false
    }

    // Unused. Needed for GestureDetector implementation
    override fun onLongPress(e: MotionEvent) {}

    // Unused. Needed for GestureDetector implementation
    override fun onShowPress(e: MotionEvent) {}
}