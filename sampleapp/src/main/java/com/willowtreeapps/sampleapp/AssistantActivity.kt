package com.willowtreeapps.sampleapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_smart_speaker_info.*


const val DEVICE_KEY = "Device_Type"

class AssistantActivity : AppCompatActivity() {

    lateinit var assistantType: AssistantType
    private val googleAssistantUrl = "https://assistant.google.com/services/invoke/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_speaker_info)
        val option = intent.extras.getInt(DEVICE_KEY, 0)
        assistantType = when (option) {
            0 -> AssistantType.GOOGLE_HOME
            else -> AssistantType.AMAZON_ALEXA
        }

        info_label.text = getString(R.string.info_label_text, assistantType.deviceName)
        device_img.setImageResource(assistantType.deviceImg)
        try_text.setOnClickListener {
            if (assistantType == AssistantType.GOOGLE_HOME) {
                val uri = Uri.parse(googleAssistantUrl)
                val intent = Intent()
                intent.data = uri
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        startPlaybackAnimation()
    }

    private fun startPlaybackAnimation() {
        revealInfoText()
    }

    private fun revealInfoText() {
        val animation = ValueAnimator.ofFloat(0f, 1f)
        animation.duration = 1000
        animation.addUpdateListener {
            new_label.alpha = it.animatedValue as Float
            info_label.alpha = it.animatedValue as Float
        }
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(p0: Animator?) {
                revealAssistantResponse()
            }
        })
        animation.startDelay = 500
        animation.start()
    }

    private fun revealAssistantResponse() {
        val animation = ValueAnimator.ofFloat(200f, 0f)
        animation.duration = 500
        animation.addUpdateListener {
            assistant_text.translationY = it.animatedValue as Float
            skill_icon.translationY = it.animatedValue as Float
        }
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(p0: Animator?) {
                firstExampleReveal()
            }

            override fun onAnimationStart(p0: Animator?) {
                assistant_text.alpha = 1f
                assistant_text.text = getString(R.string.assistant_response_text)
                assistant_text.translationY = 100f
                skill_icon.alpha = 1f
            }

        })
        animation.startDelay = 750
        animation.start()
    }

    private fun firstExampleReveal(exampleIndex: Int = 0) {
        suggestion_label.text = assistantType.examplePhrases[exampleIndex]
        val animation = ValueAnimator.ofFloat(0f, 1f)
        animation.duration = 500
        animation.addUpdateListener {
            suggestion_label.alpha = it.animatedValue as Float
        }
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(p0: Animator?) {
                if (exampleIndex < assistantType.examplePhrases.size - 1) {
                    revealExampleText(exampleIndex)
                } else {
                    revealTryText()
                }
            }
        })
        animation.startDelay = if (exampleIndex == 0) 750 else 250
        animation.start()
    }

    private fun revealExampleText(exampleIndex: Int) {
        val animation = ValueAnimator.ofFloat(1f, 0f)
        animation.duration = 750
        animation.addUpdateListener {
            suggestion_label.alpha = it.animatedValue as Float
        }
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(p0: Animator?) {
                firstExampleReveal(exampleIndex + 1)
            }
        })
        animation.startDelay = 1000
        animation.start()
    }

    private fun revealTryText() {
        val animation = ValueAnimator.ofFloat(0f, 1f)
        animation.duration = 1000
        animation.addUpdateListener {
            try_text.alpha = it.animatedValue as Float
        }
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(p0: Animator?) {
                try_text.alpha = 0f
                try_text.visibility = View.VISIBLE
            }

        })
        animation.startDelay = 500
        animation.start()
    }

}

enum class AssistantType(val deviceName: String) {

    GOOGLE_HOME("Google Home"),
    AMAZON_ALEXA("Amazon Alexa");

    val deviceImg: Int by lazy {
        when (ordinal) {
            0 -> R.drawable.google_home_crop
            else -> R.drawable.alexa_crop
        }
    }

    val examplePhrases: List<String> by lazy {
        listOf("What's in my shopping list?",
                "Add apples to my Sunday list",
                "What's on sale this week?",
                "Does my Grocr have mangos?")
    }


}