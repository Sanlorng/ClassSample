package com.sanlorng.classsample.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sanlorng.classsample.R
import com.sanlorng.kit.navigationBarLight
import com.sanlorng.kit.translucentSystemUI
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        setSupportActionBar(toolbar_message)
        toolbar_message.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        textView_message.text = intent.getStringExtra("message")
        window.translucentSystemUI(true)
        window.navigationBarLight(false)
    }
}
