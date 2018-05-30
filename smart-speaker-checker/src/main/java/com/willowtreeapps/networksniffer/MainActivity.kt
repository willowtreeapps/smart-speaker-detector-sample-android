package com.willowtreeapps.networksniffer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import de.mannodermaus.rxbonjour.BonjourEvent
import de.mannodermaus.rxbonjour.RxBonjour
import de.mannodermaus.rxbonjour.drivers.jmdns.JmDNSDriver
import de.mannodermaus.rxbonjour.platforms.android.AndroidPlatform
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_device_identifier.*
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val amazonMacAddresses = listOf("FCA667", "FCA183", "FC65DE", "F0D2F1", "F0272D",
            "B47C9C", "AC63BE", "A002DC", "8871E5", "84D6D0", "78E103", "74C246", "747548",
            "6C5697", "6854FD", "6837E9", "50F5DA", "50DCE7", "44650D", "40B4CD", "38F73D",
            "34D270", "18742E", "0C47C9", "00FC8B")

    private val chromecastService = "_googlecast._tcp"
    private val txtRecordMDKey = "md"
    private val googleHomeValue = "Google Home"
    private var disposable: Disposable? = null

    private val rxBonjour = RxBonjour.Builder()
            .platform(AndroidPlatform.create(this))
            .driver(JmDNSDriver.create())
            .create()

    data class Node(val ip: String, val mac: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_identifier)
        searchForAmazonEcho()
        searchForGoogleHome()
    }

    private fun searchForAmazonEcho() {
        val nodes = getNodes().filter { node -> !amazonMacAddresses.none { node.mac.startsWith(it, true) } }
        alexa_check_box.isChecked = !nodes.isEmpty()
    }

    //Used to sniff out Amazon Echo in ARP table
    private fun getNodes(): List<Node> {

        val nodes = mutableListOf<Node>()
        var bufferedReader: BufferedReader? = null

        try {
            bufferedReader = BufferedReader(FileReader("/proc/net/arp"))

            var line = bufferedReader.readLine()
            while (line != null) {
                val splitted = line.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (splitted.size >= 4) {
                    val ip = splitted[0]
                    val mac = splitted[3]
                    if (mac.matches("..:..:..:..:..:..".toRegex())) {
                        nodes.add(Node(ip, mac.replace(":", "")))
                    }
                }
                line = bufferedReader.readLine()
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bufferedReader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return nodes

    }

    private fun searchForGoogleHome() {
        disposable = rxBonjour.newDiscovery(chromecastService)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { event ->
                            when (event) {
                                is BonjourEvent.Added -> checkGoogleHome(event)
                            }
                        },
                        { error -> Log.d("*****", error.message) }
                )
    }

    private fun checkGoogleHome(event: BonjourEvent) {
        event.service.txtRecords[txtRecordMDKey]?.startsWith(googleHomeValue, true).let {
            google_check_box.isChecked = true
        }
    }

    override fun onResume() {
        super.onResume()
        val shouldSearch = disposable?.isDisposed ?: !google_check_box.isChecked
        if (shouldSearch) {
            searchForGoogleHome()
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }


}
