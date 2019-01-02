package com.sanlorng.classsample.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.sanlorng.classsample.R
import com.sanlorng.kit.navigationBarLight
import com.sanlorng.kit.translucentSystemUI
import kotlinx.android.synthetic.main.activity_dialog_process.*

class DialogProcessActivity : AppCompatActivity() {
    private val sampleArray = arrayOf("OnePlus One","OnePlus 2","OnePlus X","OnePlus 3","OnePlus 3T","OnePlus 5","OnePlus 5T","OnePlus 6","OnePlus 6T","OnePlus 6T McLaren")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_process)
        window.translucentSystemUI()
        window.navigationBarLight(true)
        toolbar_dialog_process.setNavigationOnClickListener { finish() }
        setSupportActionBar(toolbar_dialog_process)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        button_ok_dialog.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setTitle("简单对话框")
                .setMessage("此应用由Sanlorng创建")
                .setPositiveButton("确定") { _, _ -> Toast.makeText(this,"你点击了确定",Toast.LENGTH_SHORT).show() }
                .setNegativeButton("取消") { _, _ -> Toast.makeText(this,"你点击了取消",Toast.LENGTH_SHORT).show() }
                .setCancelable(true)
                .create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorAccent))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorAccent))
        }
        button_single_dialog.setOnClickListener {
            var select = 0
            val dialog = AlertDialog.Builder(this)
                .setTitle("单选对话框")
                .setSingleChoiceItems(sampleArray,select) { _, which -> select = which }
                .setPositiveButton("确定") { _, _ -> Toast.makeText(this,"你选择了${sampleArray[select]}",Toast.LENGTH_SHORT).show() }
                .setNegativeButton("取消") { _, _ -> Toast.makeText(this,"你点击了取消",Toast.LENGTH_SHORT).show() }
                .setCancelable(true)
                .create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorAccent))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorAccent))
        }
        button_multi_dialog.setOnClickListener {
            val booleanArray = BooleanArray(sampleArray.size)
            val dialog = AlertDialog.Builder(this)
                .setTitle("多选对话框")
                .setMultiChoiceItems(sampleArray,booleanArray) { _, which, isChecked -> booleanArray[which] = isChecked }
                .setPositiveButton("确定") { _, _ ->
                    var selectItems = ""
                    for (i in 0 until sampleArray.size)
                        if (booleanArray[i])
                            selectItems += sampleArray[i] + " "
                    Toast.makeText(this,"你选择了$selectItems",Toast.LENGTH_SHORT).show() }
                .setNegativeButton("取消") { _, _ -> Toast.makeText(this,"你点击了取消",Toast.LENGTH_SHORT).show() }
                .setCancelable(true)
                .create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorAccent))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorAccent))
        }
        button_login_dialog.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setView(R.layout.dialog_progress)
                .create()
            dialog.show()
            Thread{
                Thread.sleep(3000)
                runOnUiThread {
                    dialog.dismiss()
                }
            }.start()
        }
    }
}
