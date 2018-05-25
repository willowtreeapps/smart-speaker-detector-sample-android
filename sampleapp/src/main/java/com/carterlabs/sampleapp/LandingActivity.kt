package com.carterlabs.sampleapp

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.carterlabs.networksniffer.CheckerDelegate
import com.carterlabs.networksniffer.DeviceFoundCallback
import com.carterlabs.networksniffer.DeviceType
import com.carterlabs.sampleapp.DebugDiscoveryFragment.Companion.statusText
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_landing.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class LandingActivity : AppCompatActivity(), DeviceFoundCallback {

    private val debugFragment = DebugDiscoveryFragment()

    val checker: CheckerDelegate by lazy {
        val delegate = CheckerDelegate(this)
        delegate.setCallbackListener(this)
        delegate
    }
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        val items = listOf(Pair("Avocado", R.drawable.avocado),
                Pair("Broccoli", R.drawable.broccoli),
                Pair("Chicken", R.drawable.chicken),
                Pair("Eggs", R.drawable.eggs),
                Pair("Lettuce", R.drawable.lettuce),
                Pair("Onions", R.drawable.onions),
                Pair("Pineapples", R.drawable.pineapples),
                Pair("Steak", R.drawable.steak),
                Pair("Strawberries", R.drawable.strawberries))
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = GrocrAdapter(items)


        val logCatTask = object : DebugDiscoveryFragment.LogCatTask() {
            override fun onProgressUpdate(vararg values: String) {
                try {
                    debugFragment.statusTextView.text = values[0]
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                statusText = values[0]
                super.onProgressUpdate(*values)
            }

        }
        logCatTask.execute()
    }

    override fun onResume() {
        super.onResume()
        disposable?.dispose()
        disposable = Flowable.timer(5000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            checker.startSearching()
                        },
                        {

                        }
                )
    }

    private fun showToolTip(stringId: Int, deviceType: DeviceType) {
        val intent = Intent(this, AssistantActivity::class.java)
                .apply { putExtra(DEVICE_KEY, deviceType.ordinal) }
        assistant_tool_tip.visibility = View.VISIBLE
        assistant_tool_tip.text = getString(stringId)
        assistant_tool_tip.alpha = 1f
        val animation = ValueAnimator.ofFloat(200f, 0f)
        animation.duration = 500
        animation.addUpdateListener {
            assistant_tool_tip.translationY = it.animatedValue as Float
        }
        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                assistant_tool_tip.setOnClickListener {
                    startActivity(intent)
                    finish()
                }
            }

            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {}

        })
        animation.start()
    }

    override fun onPause() {
        super.onPause()
        checker.stopSearching()
    }


    override fun deviceFound(deviceType: DeviceType) {
        checker.stopSearching()
        disposable?.dispose()
        val stringId = when (deviceType) {
            DeviceType.GOOGLE_HOME -> R.string.assistant_tool_tip_google
            else -> R.string.assistant_tool_tip_amazon
        }
        showToolTip(stringId, deviceType)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.debug_menu_item -> {
            // Launch debug UI here
            val fragmentManager = supportFragmentManager


            val transaction = fragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.add(android.R.id.content, debugFragment)
                    .addToBackStack(null).commit()

            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun loggingCallback(msg: String) {

    }
}
