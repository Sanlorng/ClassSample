package com.sanlorng.classsample.entry

class UserEntry(var name: String, var password: String) {
    var id: Long = 0
    var age: Int = 0

    companion object {
        const val TABLE_NAME = "user"
        const val COULMN_ID = "useid"
        const val COULMN_NAME = "username"
        const val COULMN_PASS = "userpass"
        const val COULMN_AGE = "userage"

    }
}