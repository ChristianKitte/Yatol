package de.ckitte.myapplication.login

import android.os.Handler
import kotlinx.coroutines.delay

class LoginProvider {
    companion object {
        private val user: String = "a@a.de"
        private val key: String = "111111"

        data class YATOLCredentials(val user: String, val key: String)

        suspend fun ValidateCredentials(credetials: YATOLCredentials): Boolean {
            Thread.sleep(2000)

            // TODO: 25.05.2021 Implement remote validation

            return credetials.user == user && credetials.key == key
        }
    }
}