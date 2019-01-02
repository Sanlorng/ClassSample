package com.sanlorng.classsample.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sanlorng.classsample.R
import com.sanlorng.kit.navigationBarLight
import com.sanlorng.kit.translucentSystemUI
import kotlinx.android.synthetic.main.activity_hidden.*

class HiddenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hidden)
        setSupportActionBar(toolbar_hidden)
        toolbar_hidden.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        textView_hidden.text = intent.getStringExtra("message")
        window.translucentSystemUI()
        window.navigationBarLight(false)
    }
}
