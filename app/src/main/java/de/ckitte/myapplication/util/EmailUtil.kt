package de.ckitte.myapplication.util

import android.text.TextUtils
import android.util.Patterns

class EmailUtil {
    companion object {
        fun isValidEmail(email: String): Boolean {
            val testMail =
                !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
            return testMail
        }
    }
}