package de.ckitte.myapplication.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Stellt statische Funktionalität für den Login auf dem Remote System bereit
 */
class AuthenticationApi {
    companion object {
        /**
         * Ein Instanz des Firebase Authentifizierungs Objektes
         */
        private val authObj: FirebaseAuth = FirebaseAuth.getInstance()

        /**
         * Der aktuelle Nutzer
         */
        private lateinit var currentUser: FirebaseUser

        /**
         * Führt anhand des übergebenen Passwortes und eMail eine Authentifizierungsabfrage aus
         * @param eMail String Die zur Authentifizierung verwendete eMail
         * @param pwd String Das zur Authentifizierung verwendete Passwort
         * @return Boolean True, wenn die Authentifizierung erfolgreich war, ansonsten False
         */
        fun logIn(eMail: String, pwd: String): Boolean {
            try {
                authObj.signInWithEmailAndPassword(eMail, pwd)
                currentUser = authObj.currentUser!!
                return true
            } catch (e: Exception) {
                return false
            }
        }

        /**
         * Führt ein LogOut aus, indem es eine entsprechende Anforderung an das RemoteSystem übermittelt
         */
        fun logOut() {
            authObj.signOut()
        }

        /**
         * Liefert den aktuellen Nutzer der Anwendung zurück
         * @return FirebaseUser Eine Instanz von FirebaseUser
         */
        fun getCurrentUSer(): FirebaseUser {
            return currentUser
        }
    }
}