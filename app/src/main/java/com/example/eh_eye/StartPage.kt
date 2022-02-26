package com.example.eh_eye

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_startmenu.*

class StartPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startmenu)

        btn_blind.setOnClickListener{
            Intent(this,MainActivity::class.java).also{
                startActivity(it)
            }
        }

        btn_volunteer.setOnClickListener{
            Intent(this,VolunteerActivity::class.java).also{
                startActivity(it)
            }
        }
    }

}