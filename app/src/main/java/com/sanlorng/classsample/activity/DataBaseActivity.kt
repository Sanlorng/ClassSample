package com.sanlorng.classsample.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.sanlorng.classsample.R
import com.sanlorng.classsample.entry.UserEntry
import com.sanlorng.classsample.helper.UserSQLiteHelper
import com.sanlorng.kit.navigationBarLight
import com.sanlorng.kit.statusBarLight
import com.sanlorng.kit.translucentSystemUI
import kotlinx.android.synthetic.main.activity_data_base.*
import kotlinx.android.synthetic.main.user_item.view.*

class DataBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_base)
        setSupportActionBar(toolbar_data_base)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_data_base.setNavigationOnClickListener {
            finish()
        }
        window.translucentSystemUI()
        window.navigationBarLight(true)
        Thread {
            val dbHelper = UserSQLiteHelper(this)
            val db = dbHelper.readableDatabase
            val cursor = db.query(UserEntry.TABLE_NAME, null, null, null, null, null, null)
            val items = ArrayList<UserEntry>()
            while (cursor.moveToNext()){
                val name = cursor.getString(cursor.getColumnIndex(UserEntry.COULMN_NAME))
                val pass = cursor.getString(cursor.getColumnIndex(UserEntry.COULMN_PASS))
                val id = cursor.getLong(cursor.getColumnIndex(UserEntry.COULMN_ID))
                val age = cursor.getInt(cursor.getColumnIndex(UserEntry.COULMN_AGE))
                val user = UserEntry(name,pass)
                user.id = id
                user.age = age
                items.add(user)
            }
            cursor.close()
            runOnUiThread {
                list_user_data.adapter = object : ArrayAdapter<UserEntry>(this,R.layout.user_item,items){
                    @SuppressLint("ViewHolder")
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val user = getItem(position)
                        val view = layoutInflater.inflate(R.layout.user_item,parent,false)
                        view.run {
                            user?.run {
                                textView_user_name.text = name
                                textView_user_pass.text = password
                            }
                        }
                        return view
                    }
                }
            }
        }.start()
    }
}
