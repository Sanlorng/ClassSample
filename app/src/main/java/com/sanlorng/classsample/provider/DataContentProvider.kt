package com.sanlorng.classsample.provider

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import com.sanlorng.classsample.entry.UserEntry
import com.sanlorng.classsample.helper.UserSQLiteHelper
import java.lang.Exception
import java.util.*

class DataContentProvider : ContentProvider() {
    companion object {
        private const val TABLE_USER = UserEntry.TABLE_NAME
        private const val TABLE_USER_ID = UserEntry.COULMN_ID
        private const val AUTHORITY="com.sanlorng.classsample"
        private const val MATCH_CODE_ITEM = 0
        private const val MATCH_CODE_DIR = 1
        private lateinit var db:SQLiteDatabase
        private val uri = Uri.parse("content://$AUTHORITY/$TABLE_USER")
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).also {
            it.addURI(AUTHORITY, "$TABLE_USER/#", MATCH_CODE_ITEM)
            it.addURI(AUTHORITY, TABLE_USER, MATCH_CODE_DIR)
        }
    }
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return when(uriMatcher.match(uri)) {
            MATCH_CODE_ITEM -> db.delete(TABLE_USER,"$TABLE_USER_ID = ?", arrayOf(uri.pathSegments[1]))
            MATCH_CODE_DIR -> db.delete(TABLE_USER,selection,selectionArgs)
            else -> 0
        }
    }

    override fun getType(uri: Uri): String? {
        return  when(uriMatcher.match(uri)) {
            MATCH_CODE_DIR -> "vnv.android.cursor.dir/vnd.$AUTHORITY.$TABLE_USER"
            MATCH_CODE_ITEM -> "vnv.android.cursor.item/vnd.$AUTHORITY.$TABLE_USER"
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues): Uri? {
        return when(uriMatcher.match(uri)) {
            MATCH_CODE_ITEM ->  Uri.parse("content://$AUTHORITY/$TABLE_USER/${db.insert(TABLE_USER, null, values)}")
            MATCH_CODE_DIR -> Uri.parse("content://$AUTHORITY/$TABLE_USER/${db.insert(TABLE_USER, null, values)}")
            else -> null
        }
    }

    override fun onCreate(): Boolean {
        return try {
            db = UserSQLiteHelper(context!!).writableDatabase
            false
        }catch (e: Exception) {
            false
        }
    }

    @SuppressLint("Recycle")
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            MATCH_CODE_ITEM -> db.query(TABLE_USER,projection,"$TABLE_USER_ID = ?", arrayOf(uri.pathSegments[1]),null,null,sortOrder)
            MATCH_CODE_DIR ->  db.query(TABLE_USER, projection, selection, selectionArgs, null, null, sortOrder)
            else -> null
        }
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return when(uriMatcher.match(uri)) {
            MATCH_CODE_DIR -> db.update(TABLE_USER,values,selection,selectionArgs)
            MATCH_CODE_ITEM -> db.update(TABLE_USER,values,"$TABLE_USER_ID = ?", arrayOf(uri.pathSegments[1]))
            else -> 0
        }
    }
}
