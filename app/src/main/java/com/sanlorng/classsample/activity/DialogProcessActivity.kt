package com.sanlorng.classsample.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.sanlorng.classsample.R
import com.sanlorng.kit.navigationBarLight
import com.sanlorng.kit.translucentSystemUI
import kotlinx.android.synthetic.main.activity_dialog_process.*
import kotlinx.coroutines.*



class DialogProcessActivity : AppCompatActivity() {
    private val sampleArray = arrayOf(
        "OnePlus One",
        "OnePlus 2",
        "OnePlus X",
        "OnePlus 3",
        "OnePlus 3T",
        "OnePlus 5",
        "OnePlus 5T",
        "OnePlus 6",
        "OnePlus 6T",
        "OnePlus 6T McLaren"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_process)
        window.translucentSystemUI(true)
        window.navigationBarLight(true)
        setSupportActionBar(toolbar_dialog_process)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_dialog_process.setNavigationOnClickListener { finish() }
        button_ok_dialog.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setTitle("简单对话框")
                .setMessage("此应用由Sanlorng创建")
                .setPositiveButton("确定") { _, _ -> Toast.makeText(this, "你点击了确定", Toast.LENGTH_SHORT).show() }
                .setNegativeButton("取消") { _, _ -> Toast.makeText(this, "你点击了取消", Toast.LENGTH_SHORT).show() }
                .setCancelable(true)
                .create()
            dialog.show()
            dialog.textButtonStyle()
        }
        button_single_dialog.setOnClickListener {
            var select = 0
            val dialog = AlertDialog.Builder(this)
                .setTitle("单选对话框")
                .setSingleChoiceItems(sampleArray, select) { _, which -> select = which }
                .setPositiveButton("确定") { _, _ ->
                    Toast.makeText(
                        this,
                        "你选择了${sampleArray[select]}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("取消") { _, _ -> Toast.makeText(this, "你点击了取消", Toast.LENGTH_SHORT).show() }
                .setCancelable(true)
                .create()
            dialog.show()
            dialog.textButtonStyle()
        }
        button_multi_dialog.setOnClickListener {
            val booleanArray = BooleanArray(sampleArray.size)
            val dialog = AlertDialog.Builder(this)
                .setTitle("多选对话框")
                .setMultiChoiceItems(sampleArray, booleanArray) { _, which, isChecked ->
                    booleanArray[which] = isChecked
                }
                .setPositiveButton("确定") { _, _ ->
                    var selectItems = ""
                    for (i in 0 until sampleArray.size)
                        if (booleanArray[i])
                            selectItems += sampleArray[i] + " "
                    Toast.makeText(this, "你选择了$selectItems", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消") { _, _ -> Toast.makeText(this, "你点击了取消", Toast.LENGTH_SHORT).show() }
                .setCancelable(true)
                .create()
            dialog.show()
            dialog.textButtonStyle()
        }
        button_login_dialog.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setView(R.layout.dialog_progress)
                .create()
            dialog.show()
            GlobalScope.launch {
                delay(3000)
                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                }
            }
        }
    }
}
fun AlertDialog.textButtonStyle():AlertDialog {
    val color = context.getColor(R.color.colorAccent)
    val typedValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue , true)
    val attribute = intArrayOf(android.R.attr.selectableItemBackground)
    val typedArray = context.theme.obtainStyledAttributes(typedValue.resourceId, attribute)
    arrayOf(AlertDialog.BUTTON_NEGATIVE,AlertDialog.BUTTON_NEUTRAL,AlertDialog.BUTTON_POSITIVE).forEach {
        getButton(it)?.apply {
            setTextColor(color)
            background = typedArray.getDrawable(0)
        }
    }
    return this
}
