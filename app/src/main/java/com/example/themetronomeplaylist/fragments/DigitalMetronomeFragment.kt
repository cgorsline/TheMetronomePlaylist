package com.example.themetronomeplaylist.fragments

import android.os.Bundle
import com.example.themetronomeplaylist.R
import android.content.Context
import android.os.Build
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.themetronomeplaylist.services.MetronomeService
import com.example.themetronomeplaylist.views.RotaryKnobView
import kotlinx.android.synthetic.main.fragment_digital_metronome.*

const val DEFAULT_BPM = 120
// Main metronome fragment
abstract class DigitalMetronomeFragment : AbstractMetronomeFragment(), RotaryKnobView.RotaryKnobListener, TextView.OnEditorActionListener, View.OnTouchListener {

    private var lastTapMil: Long = 0

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_digital_metronome, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playButton.setOnClickListener {this.play()}
        pauseButton.setOnClickListener {this.pause()}
        rhythmButton.setOnClickListener {this.nextRhythm()}
        toneButton.setOnClickListener {this.nextTone()}
        tapTempoButton.setOnClickListener {this.tapTempo()}
        emphasisButton.setOnClickListener {
            val isEmphasis = metronomeService?.toggleEmphasis()
            beatsView.isEmphasis = isEmphasis!!
        }
        beatsUpButton.setOnClickListener {this.updateBeatsUp()}
        beatsDownButton.setOnClickListener {this.updateBeatsDown()}
        rotaryKnob.listener = this
        val bpm = when (metronomeService?.bpm) {
            null -> DEFAULT_BPM
            else -> metronomeService?.bpm
        }
        rotaryKnob.setKnobPositionByValue(bpm!!)
        setBpmText(bpm)
        bpmText.isCursorVisible = false
        bpmText.setOnEditorActionListener(this)
        bpmText.setOnTouchListener(this)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateBeatsUp() {
        val beats = metronomeService?.setBeatsUp()
        beatsView.beatsPerMeasure = beats!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateBeatsDown() {
        val beats = metronomeService?.setBeatsDown()
        beatsView.beatsPerMeasure = beats!!
    }

    private fun tapTempo() {
        val currentMil = System.currentTimeMillis()
        val difference = currentMil - lastTapMil
        val calculatedBpm = (60000 / difference).toInt()
        val bpm = metronomeService?.setBpm(calculatedBpm)
        bpmText.setText(getBpmText(bpm!!))
        lastTapMil = currentMil
    }

    private fun setBpmText(bpm: Int) {
        bpmText.setText(getBpmText(bpm))
    }

    private fun getBpmText(bpm: Int): String {
        return if (bpm >= 100) "$bpm" else " $bpm"
    }

    private fun nextTone() {
        val tone = metronomeService?.nextTone()
        if (tone != null) {
            tonesView.selectTone(tone)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nextRhythm() {
        val drawable = when (metronomeService?.nextRhythm()) {
            MetronomeService.Rhythm.QUARTER -> R.drawable.ic_quarter_note
            MetronomeService.Rhythm.EIGHTH -> R.drawable.ic_eighth_note
            MetronomeService.Rhythm.SIXTEENTH ->  R.drawable.ic_sixteenth_note
            null -> R.drawable.ic_quarter_note
        }
        rhythmImage.setImageDrawable(
            activity?.applicationContext?.let {
                ContextCompat.getDrawable(it, drawable)
            }
        )
        beatsView.resetBeats(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun play() {
        beatsView.resetBeats(true)
        metronomeService?.play()
    }

    private fun pause() {
        metronomeService?.pause()
    }

    // Rotary listener interface starts here

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        bpmText.isCursorVisible = true
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            val imm = v!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            val bpm = v.text.toString().toInt()
            metronomeService?.setBpm(bpm)
            rotaryKnob.setKnobPositionByValue(bpm)
            bpmText.isCursorVisible = false
            return true
        }
        bpmText.isCursorVisible = false
        return false
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v == bpmText) {
            v as EditText
            v.isCursorVisible = true
        }
        return false
    }
}