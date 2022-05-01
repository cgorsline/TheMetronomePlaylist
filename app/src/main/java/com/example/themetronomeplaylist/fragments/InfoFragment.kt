package com.example.themetronomeplaylist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.themetronomeplaylist.R
import android.text.method.LinkMovementMethod
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.themetronomeplaylist.R
import kotlinx.android.synthetic.main.info_fragment.*
import kotlinx.android.synthetic.main.digital_metronome_fragment.*

class InfoFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aboutText.movementMethod = LinkMovementMethod.getInstance()
    }
}