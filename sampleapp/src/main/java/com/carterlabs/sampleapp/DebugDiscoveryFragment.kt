package com.carterlabs.sampleapp

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import android.os.AsyncTask
import android.support.v4.app.DialogFragment
import java.util.concurrent.atomic.AtomicBoolean
import java.io.Reader
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import kotlin.properties.Delegates


class DebugDiscoveryFragment : DialogFragment() {

    lateinit var statusTextView: TextView

    companion object {
        var statusText: String? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout to use as dialog or embedded fragment
        val layout = inflater.inflate(R.layout.fragment_debug_discovery, container, false)
        statusTextView = layout.findViewById(R.id.service_status)
        statusTextView.movementMethod = ScrollingMovementMethod()
        statusTextView.text = statusText
        return layout
    }

    open class LogCatTask : AsyncTask<Void, String, Void>() {
        var run = AtomicBoolean(true)

        override fun doInBackground(vararg params: Void): Void? {
            try {
                //Runtime.getRuntime().exec("logcat -v raw -d -s smart-speaker-checker")
                val process = Runtime.getRuntime().exec("logcat -v raw -d -s smart-speaker-checker")
                val bufferedReader = BufferedReader(InputStreamReader(process.inputStream) as Reader?)
                val log = StringBuilder()
                var line: String? = ""
                while (run.get()) {
                    line = bufferedReader.readLine()
                    if (line != null) {
                        log.append(line)
                        log.append("\n")
                        publishProgress(log.toString())
                    }
                    line = null
                    Thread.sleep(10)
                }
            } catch (ex: Exception) {

            }

            return null
        }

}

}