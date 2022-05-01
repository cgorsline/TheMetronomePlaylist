package com.example.themetronomeplaylist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.themetronomeplaylist.services.MetronomeService

abstract class AbstractMetronomeFragment : Fragment(), MetronomeService.TickListener{
    protected var isBound = false
    protected var metronomeService: MetronomeService? =null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindService()
    }

    private fun bindService() {
        activity?.bindService(Intent(activity,MetronomeService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        isBound = true
    }

    protected val MConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            metronomeService = (service as MetronomeService.MetronomeBinder).getService()
            metronomeService?.addTickListener(this@AbstractMetronomeFragment)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            metronomeService = null
            isBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            metronomeService?.removeTickListener(this)
            activity!!.unbindService(mConnection)
            isBound = false
        }
    }
}