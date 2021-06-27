package de.ckitte.myapplication.login

import de.ckitte.myapplication.firestore.AuthenticationApi

/**
 * Liefert HighLevel Funktionalität für den Login Vorgang
 */
class LoginProvider {
    companion object {
        /**
         * True (1), wenn der aktuelle Nutzer angemeldet ist, sonst False
         */
        @Volatile
        private var isLoggedIn: Boolean = false

        /**
         * Repräsentiert die Credentials der Anwendung
         * @property user String Der Nutzer
         * @property key String Das Passwort
         * @constructor
         */
        data class YATOLMailCredentials(val user: String, val key: String)

        /**
         * True (1), wenn der aktuelle Nutzer angemeldet ist, sonst False
         * @return Boolean
         */
        fun isLoggedIn(): Boolean {
            return isLoggedIn
        }

        /**
         * Iniziiert den Logvorgang
         * @param credentials YATOLMailCredentials Die zu verwendenden Credentials
         * @return Boolean True (1), wenn der Versuch erfolgreich war, sonst False
         */
        suspend fun LogIn(credentials: YATOLMailCredentials): Boolean {
            Thread.sleep(2000)
            isLoggedIn = AuthenticationApi.logIn(credentials.user, credentials.key)
            return isLoggedIn
        }

        /**
         * Iniziiert einen Abmeldevorgang
         */
        suspend fun LogOut() {
            AuthenticationApi.logOut()
        }
    }
}