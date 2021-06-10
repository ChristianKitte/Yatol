package de.ckitte.myapplication.login

import de.ckitte.myapplication.firestore.AuthenticationApi

class LoginProvider {
    companion object {
        @Volatile
        private var isLoggedIn: Boolean = false

        data class YATOLMailCredentials(val user: String, val key: String)

        fun isLoggedIn(): Boolean {
            return isLoggedIn
        }

        suspend fun LogIn(credentials: YATOLMailCredentials): Boolean {
            Thread.sleep(2000)
            isLoggedIn = AuthenticationApi.logIn(credentials.user, credentials.key)
            return isLoggedIn
        }

        suspend fun LogOut() {
            AuthenticationApi.logOut()
        }
    }
}