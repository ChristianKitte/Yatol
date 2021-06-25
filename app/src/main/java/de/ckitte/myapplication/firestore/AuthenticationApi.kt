package de.ckitte.myapplication.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 *
 */
class AuthenticationApi {
    companion object {
        /**
         *
         */
        private val authObj: FirebaseAuth = FirebaseAuth.getInstance()

        /**
         *
         */
        private lateinit var currentUser: FirebaseUser

        /**
         *
         * @param eMail String
         * @param pwd String
         * @return Boolean
         */
        suspend fun logIn(eMail: String, pwd: String): Boolean {
            try {
                authObj.signInWithEmailAndPassword(eMail, pwd)
                currentUser = authObj.currentUser!!
                return true
            } catch (e: Exception) {
                return false
            }
        }

        /**
         *
         */
        suspend fun logOut() {
            authObj.signOut()
        }

        fun getCurrentUSer(): FirebaseUser {
            return currentUser
        }
    }

}