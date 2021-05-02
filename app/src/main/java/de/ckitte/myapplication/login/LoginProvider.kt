package de.ckitte.myapplication.login

class LoginProvider {
    companion object {
        private val user: String = "yattol@hallo.ms"
        private val key: String = "01234"

        data class YATOLCredentials(val user: String, val key: String)

        fun ValidateCredentials(credetials: YATOLCredentials): Boolean {
            return credetials.user == user && credetials.key == key
        }
    }
}