package de.ckitte.myapplication.util

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract

/**
 * Gibt den aktuellen Status eines Kontaktes an
 *
 * SAVE ==> Der default Zustand
 *
 * ADDED ==> Der Zustand wurde hinzugefügt, aber noch nicht commited
 *
 * DELETED ==> Der Zustand wurde gelöscht, aber noch nicht commited
 */
enum class ToDoContactState {
    Save,
    Added,
    Deleted
}

/**
 * Gibt den zu einer URI gehörenden Display Namen eines Kontaktes zurück
 * @param uri Uri Die URI eines Kontaktes
 * @param contentResolver ContentResolver Ein Gültiges ContentResolver Objekt
 * @return String Der Anzeigename
 */
fun getDisplayNameByUri(uri: Uri, contentResolver: ContentResolver): String {
    var displayName = ""

    contentResolver.let {
        // querying contact data store
        val cursor: Cursor? =
            it.query(
                uri,
                null,
                null,
                null,
                null
            )

        cursor?.let {
            if (it.moveToFirst()) {
                displayName =
                    it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            }
        }
    }

    return displayName
}

/**
 * Gibt die zu einer URI gehörende Telefonnummer eines Kontaktes zurück
 * @param uri Uri Die URI eines Kontaktes
 * @param contentResolver ContentResolver Ein Gültiges ContentResolver Objekt
 * @return String Die Telefonnummer
 */
fun getPhoneNumberByUri(uri: Uri, contentResolver: ContentResolver): String {
    var phoneNumber = ""

    contentResolver.let {
        val cursor: Cursor? =
            it.query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            val hasPhone =
                cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

            if (hasPhone > 0) {
                val cursor2: Cursor = it.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf<String>(id),
                    null
                )!!

                if (cursor2.moveToFirst()) {
                    phoneNumber =
                        cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    cursor2.close()
                }
            }

            cursor.close()
        }
    }

    return phoneNumber
}

/**
 * Gibt die zu einer URI gehörende eMail Adresse eines Kontaktes zurück
 * @param uri Uri Die URI eines Kontaktes
 * @param contentResolver ContentResolver Ein Gültiges ContentResolver Objekt
 * @return String Die eMail Adresse
 */
fun getEmailAdressByUri(uri: Uri, contentResolver: ContentResolver): String {
    var eMailAdress = ""

    contentResolver.let {
        val cursor: Cursor? =
            it.query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

            val cursor2 = it.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                arrayOf<String>(id),
                null
            )!!

            if (cursor2.moveToFirst()) {
                // Hier muss tatsächlich DATA hin statt ADRESS, was ich wenig intuitiv finde
                eMailAdress =
                    cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                cursor2.close()
            }

            cursor.close()
        }
    }

    return eMailAdress
}