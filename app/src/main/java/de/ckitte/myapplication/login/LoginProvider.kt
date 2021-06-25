package de.ckitte.myapplication.login

import de.ckitte.myapplication.firestore.AuthenticationApi

/**
 *
 */
class LoginProvider {
    companion object {
        /**
         *
         */
        @Volatile
        private var isLoggedIn: Boolean = false

        /**
         *
         * @property user String
         * @property key String
         * @constructor
         */
        data class YATOLMailCredentials(val user: String, val key: String)

        /**
         *
         * @return Boolean
         */
        fun isLoggedIn(): Boolean {
            return isLoggedIn
        }

        /**
         *
         * @param credentials YATOLMailCredentials
         * @return Boolean
         */
        suspend fun LogIn(credentials: YATOLMailCredentials): Boolean {
            Thread.sleep(2000)
            isLoggedIn = AuthenticationApi.logIn(credentials.user, credentials.key)
            return isLoggedIn
        }

        /**
         *
         */
        suspend fun LogOut() {
            AuthenticationApi.logOut()
        }
    }
}