package de.ckitte.myapplication.login

import de.ckitte.myapplication.firestore.AuthenticationApi

class LoginProvider {
    companion object {
        data class YATOLMailCredentials(val user: String, val key: String)

        suspend fun LogIn(credentials: YATOLMailCredentials): Boolean {
            Thread.sleep(2000)
            return AuthenticationApi.logIn(credentials.user, credentials.key)
        }

        suspend fun LogOut() {
            AuthenticationApi.logOut()
        }
    }
}