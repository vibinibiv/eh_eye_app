package com.example.eh_eye

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tts = TextToSpeech(this, this)

        btn_read.setOnClickListener{
            speakOut("Reed text?")

        }
        btn_read.setOnLongClickListener {
            Intent(this,OCRReadActivity::class.java).also{
                startActivity(it)
            }
            true
        }

        btn_detect.setOnClickListener{
            speakOut("Detect,objects!")
        }

        btn_detect.setOnLongClickListener {
            Intent(this,DetectActivity::class.java).also{
                startActivity(it)
            }
            true
        }

        btn_help.setOnClickListener{
            speakOut("Call for Help?")
        }

        btn_help.setOnLongClickListener{
            Intent(this, CallActivity::class.java).also{
                startActivity(it)
            }
            true
        }
    }


    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {
                //buttonSpeak!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }

    }

    private fun speakOut(text :String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    public override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}