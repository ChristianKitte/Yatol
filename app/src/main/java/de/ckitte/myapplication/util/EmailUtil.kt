package de.ckitte.myapplication.util

import android.text.TextUtils
import android.util.Patterns

/**
 * Enthält statische Hilfsmethoden für eMail Adressen
 */
class EmailUtil {
    companion object {
        /**
         * Prüft einen String danach, ob es sich formal um eine gültige eMail Adresse handelt. Hierbei wird
         * auf die Android interne Funktionalität zurück gegriffen.
         * @param email String Der die eMail Adresse repräsentierende String
         * @return Boolean True, wenn es formal eine gültige Adresse ist, ansonsten False
         */
        fun isValidEmail(email: String): Boolean {
            val testMail =
                !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
            return testMail
        }
    }
}