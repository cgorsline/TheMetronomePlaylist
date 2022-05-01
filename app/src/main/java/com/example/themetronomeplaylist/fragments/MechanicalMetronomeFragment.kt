package com.example.themetronomeplaylist.fragments

import android.icu.text.Transliterator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.themetronomeplaylist.R
import android.util.Log
import android.view.Gravity.CENTER
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.GridLayout.CENTER
import kotlinx.android.synthetic.main.mechanical_metronome_fragment.*
import com.example.themetronomeplaylist.R

class MechanicalMetronomeFragment : AbstrtactMetronomeFragment() {

    private var duration = 1000L
    private var position = Position.CENTER
    private lateinit var midToRightAnimation: Animation
    private lateinit var rightToLeftAnimation: Animation
    private lateinit var leftToRightAnimation: Animation

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.mechanical_metronome_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        midToRightAnimation = AnimationUtils.loadAnimation(context, R.anim.mid_to_right)
        leftToRightAnimation = AnimationUtils.loadAnimation(context, R.anim.left_to_right)
        rightToLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.right_to_left)
    }

    private fun animateArm(duration: Long) {
        Log.i("Mechanical", "animate with $duration")

        val animation = when(position) {
            Position.LEFT -> {
                this.position = Position.RIGHT
                leftToRightAnimation
            }
            Position.RIGHT -> {
                this.position = Position.LEFT
                rightToLeftAnimation
            }
            else -> {
                this.position = Position.RIGHT
                midToRightAnimation
            }
        }
        animation.duration = duration
        metronomeArmView.startAnimation(animation)
    }

    override fun onTick(interval: Int)  {
        duration = interval.toLong()
        activity?.runOnUiThread {
            animateArm(duration)
        }
    }

    enum class Position(val value: Int) {
        CENTER(0),
        LEFT(1),
        RIGHT(2);
    }
}