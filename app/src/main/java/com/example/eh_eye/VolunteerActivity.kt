package com.example.eh_eye

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_volunteer.*

class VolunteerActivity : AppCompatActivity() {

    var activeState = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer)

        btnActive.setOnClickListener{
//            Intent(this,MainActivity::class.java).also{
//                startActivity(it)
//            }
            if(activeState==1) {
                btnActive.text = "InActive"
                btnActive.alpha = 0.5F
                activeState = 0
                btnEnterCall.visibility = View.INVISIBLE;

            }else{
                btnActive.text = "Active"
                btnActive.alpha = 1F
                activeState = 1
                btnEnterCall.visibility = View.VISIBLE;

            }

        }

        btnEnterCall.setOnClickListener{
            Intent(this,CallActivity::class.java).also{
                startActivity(it)
            }
        }
    }

}