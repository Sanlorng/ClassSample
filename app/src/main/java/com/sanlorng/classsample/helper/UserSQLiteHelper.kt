package com.sanlorng.classsample.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.sanlorng.classsample.entry.UserEntry

class UserSQLiteHelper(val context: Context):SQLiteOpenHelper(context, DB_NAME,null,DB_VERSION){
    companion object {
        const val CREATE_TABLE_SQL = "CREATE TABLE ${UserEntry.TABLE_NAME} (" +
                "${UserEntry.COULMN_ID} integer primary key autoincrement, " +
                "${UserEntry.COULMN_NAME} VARCHAR(30), " +
                "${UserEntry.COULMN_PASS} VARCHAR(30), " +
                "${UserEntry.COULMN_AGE} INTEGER" +
                ")"
        const val DELETE_ENTRYS = "DROP TABLE IF EXISTS ${UserEntry.TABLE_NAME}"
        const val DB_NAME = "user.db"
        const val DB_VERSION = 1

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_SQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DELETE_ENTRYS)
    }
}