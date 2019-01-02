package com.sanlorng.classsample.helper

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper

class UserSQLiteHelper(val context: Context):SQLiteOpenHelper(context, DB_NAME,null,DB_VERSION){

    companion object {
        const val CREATE_TABLE_SQL = "CREATE TABLE"
        const val DB_NAME = "user.db"
        const val DB_VERSION = 1
    }
}