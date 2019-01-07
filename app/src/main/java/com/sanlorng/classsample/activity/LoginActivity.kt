package com.sanlorng.classsample.activity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.sanlorng.classsample.R
import com.sanlorng.classsample.entry.UserEntry
import com.sanlorng.classsample.helper.UserSQLiteHelper
import com.sanlorng.kit.defaultSharedPreference
import com.sanlorng.kit.translucentSystemUI
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.IllegalStateException

class LoginActivity : AppCompatActivity() {
    lateinit var loginTask:LoginTask
    private lateinit var phone:String
    private lateinit var pass:String
    private lateinit var dbHelper: UserSQLiteHelper
    private lateinit var db:SQLiteDatabase
    private var isRegister = false
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
            isRegister = false
            tryLoginTask()
        }
        button_try_sign_up.setOnClickListener {
            isRegister = true
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
        dbHelper = UserSQLiteHelper(this)
        db = dbHelper.writableDatabase
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
            else -> {loginTask = LoginTask();loginTask.execute()}
        }
    }
    @SuppressLint("StaticFieldLeak")
    inner class LoginTask : AsyncTask<Void,Void,Boolean>(){
        lateinit var dialog: AlertDialog
        private var failureString = ""
        override fun onPreExecute() {
            super.onPreExecute()
            dialog = AlertDialog.Builder(this@LoginActivity)
                .setView(R.layout.dialog_progress)
                .create()
            dialog.show()
            button_try_login.isEnabled = false
            button_try_sign_up.isEnabled = false
        }
        override fun doInBackground(vararg params: Void?): Boolean {
            val cursor = db.query(UserEntry.TABLE_NAME, arrayOf(UserEntry.COULMN_NAME,UserEntry.COULMN_PASS),UserEntry.COULMN_NAME + " = ?",
                arrayOf(phone),null,null,null)
            if (cursor.count == 0){
                return if (!isRegister) {
                    failureString = "尚未注册，请先注册"
                    cursor.close()
                    false
                }else{
                    val value = ContentValues()
                    value.put(UserEntry.COULMN_NAME,phone)
                    value.put(UserEntry.COULMN_PASS,pass)
                    value.put(UserEntry.COULMN_AGE,0)
                    db.insert(UserEntry.TABLE_NAME,null,value)
                    value.clear()
                    cursor.close()
                    true
                }
            }
            if (isRegister){
                cursor.close()
                return false
            }
            cursor.moveToFirst()
            if (cursor.getString(cursor.getColumnIndex(UserEntry.COULMN_PASS))!=pass&&!isRegister){
                failureString = "密码有误"
                cursor.close()
                return false
            }
            cursor.close()
            return true
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            dialog.cancel()
            button_try_sign_up.isEnabled = true
            button_try_login.isEnabled = true
            if (result){
                if (!isRegister) {
                    if (checkBox_save_user_info.isChecked)
                        defaultSharedPreference.edit {
                            putBoolean("is_save_user_info", checkBox_save_user_info.isChecked)
                            putString("phone", phone)
                            putString("pass", pass)
                        }

                    Toast.makeText(this@LoginActivity, "登陆成功", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }else{
                if (!isRegister){
                if (failureString == "尚未注册，请先注册")
                    Toast.makeText(this@LoginActivity,failureString,Toast.LENGTH_SHORT).show()
                else{
                    input_pass.apply {  error = failureString;requestFocus()}
                }
            }else
                    Toast.makeText(this@LoginActivity,"已经注册了该用户",Toast.LENGTH_SHORT).show()
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
