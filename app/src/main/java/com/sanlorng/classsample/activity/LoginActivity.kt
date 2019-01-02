package com.sanlorng.classsample.activity

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.sanlorng.classsample.R
import com.sanlorng.kit.defaultSharedPreference
import com.sanlorng.kit.translucentSystemUI
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val loginTask : LoginTask by lazy(LazyThreadSafetyMode.NONE, this::LoginTask)
    private lateinit var phone:String
    private lateinit var pass:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.translucentSystemUI(true)
        setSupportActionBar(toolbar_login)
        toolbar_login.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        toolbar_login.navigationIcon = getDrawable(R.drawable.ic_close_black_24dp)
        button_try_login.setOnClickListener {
            tryLoginTask()
        }
        if (defaultSharedPreference.getBoolean("is_save_user_info",false)){
            checkBox_save_user_info.isChecked = true
            input_pass.editableText.append( defaultSharedPreference.getString("pass",""))
            input_phone.editableText.append(defaultSharedPreference.getString("phone",""))
        }else{
            defaultSharedPreference.edit {
                putString("phone",null)
                putString("pass",null)
            }
        }
    }
    private fun tryLoginTask(){
        phone =  input_phone.editableText.toString()
        pass = input_pass.editableText.toString()
        input_phone.error = null
        input_pass.error = null
        when {
            phone.isEmpty() -> input_phone.apply{error = "未填写密码";requestFocus()}
            pass.isEmpty() -> input_pass.apply {  error = "未填写密码";requestFocus()}
            phone.length != 11 -> input_phone.apply {  error = "填写的格式有误";requestFocus()}
            pass.length < 6 -> input_pass.apply {  error = "密码长度不够";requestFocus()}
            else -> loginTask.execute()
        }
    }
    @SuppressLint("StaticFieldLeak")
    inner class LoginTask : AsyncTask<Void,Void,Boolean>(){
        lateinit var dialog: AlertDialog
        override fun onPreExecute() {
            super.onPreExecute()
            dialog = AlertDialog.Builder(this@LoginActivity)
                .setView(R.layout.dialog_progress)
                .create()
            dialog.show()
        }
        override fun doInBackground(vararg params: Void?): Boolean {
            Thread.sleep(1000)
            return true
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            dialog.cancel()
            if (result){
                if(checkBox_save_user_info.isChecked)
                    defaultSharedPreference.edit {
                        putBoolean("is_save_user_info",checkBox_save_user_info.isChecked)
                        putString("phone",phone)
                        putString("pass",pass)
                    }
                Toast.makeText(this@LoginActivity,"登陆成功",Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        defaultSharedPreference.edit {
            putBoolean("is_save_user_info",checkBox_save_user_info.isChecked)
        }
    }
}
