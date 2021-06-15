package de.ckitte.myapplication.util

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract

enum class ContactState {
    Save,
    Added,
    Deleted
}

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