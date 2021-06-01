package de.ckitte.myapplication.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.model.DocumentKey
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.internal.DiskLruCache
import de.ckitte.myapplication.firestore.firestoreEntities.firestoreToDoGroup
import de.ckitte.myapplication.firestore.firestoreEntities.firestoreToDoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreApi {
    var db = FirebaseFirestore.getInstance()

    companion object {
        @Volatile
        lateinit var defaultGroupID: String

        val getToDoItemCollection = "ToDoItems"
        val getToDoGroupCollection = "Groups"
        val getToDoContactCollection = "Contacts"
    }

    suspend fun emptyStore() {
        deleteItemsOfCollection(getToDoContactCollection)
        deleteItemsOfCollection(getToDoItemCollection)
        deleteItemsOfCollection(getToDoGroupCollection)
    }

    private fun deleteItemsOfCollection(collection: String): Boolean {
        val targetCollection = Firebase.firestore.collection(collection)
        var isSuccessful = false

        targetCollection.document(targetCollection.id).delete().addOnCompleteListener {
            isSuccessful = it.isSuccessful
        }

        return isSuccessful
    }

    suspend fun syncOnlineStore() {
        //Firestore mit lokalen Daten füllen
    }

    suspend fun synLocalStore() {
        //SQLite mit Online Daten füllen
    }

    suspend fun ensureDefaultGroup() {
        val targetCollection = Firebase.firestore.collection(getToDoGroupCollection)
        val resultSnapshot = targetCollection.whereEqualTo("toDoGroupId", "default").get()

        resultSnapshot.addOnCompleteListener {
            if (it.isSuccessful) {
                defaultGroupID = "default"
            } else {
                val newGroup = firestoreToDoGroup(
                    toDoGroupId = "default",
                    toDoGroupIsDefault = true,
                    toDoGroupTitle = "Default",
                    toDoGroupDescription = "Default Gruppe",
                    user = ""
                )

                CoroutineScope(Dispatchers.IO).launch {
                    insertToDoGroup(getToDoGroupCollection, newGroup)

                    withContext(Dispatchers.Main) {
                        defaultGroupID = "default"
                    }
                }
            }
        }
    }

    suspend fun insertToDoItem(collection: String, vararg firestoreToDoItems: firestoreToDoItem) {
        val targetCollection = Firebase.firestore.collection(collection)

        for (firestoreToDoItem in firestoreToDoItems) {
            targetCollection.add(firestoreToDoItem)
        }
    }

    suspend fun deleteToDoItem(collection: String, vararg firestoreToDoItems: firestoreToDoItem) {
        val targetCollection = Firebase.firestore.collection(collection)

        for (firestoreToDoItem in firestoreToDoItems) {
            if (firestoreToDoItem.toDoId.isNotBlank()) {
                targetCollection.document(firestoreToDoItem.toDoId).delete()
            }
        }
    }

    suspend fun updateOrAddToDoItem(collection: String, vararg firestoreToDoItems: firestoreToDoItem) {
        val targetCollection = Firebase.firestore.collection(collection)

        for (firestoreToDoItem in firestoreToDoItems) {
            if (firestoreToDoItem.toDoId.isNotBlank()) {
                val map = getMutableMapFromToDoItem(firestoreToDoItem)

                CoroutineScope(Dispatchers.IO).launch {
                    targetCollection.document(firestoreToDoItem.toDoId).set(map, SetOptions.merge())
                }
            } else {
                insertToDoItem(getToDoItemCollection, firestoreToDoItem)
            }
        }
    }

    private fun getMutableMapFromToDoItem(firestoreToDoItem: firestoreToDoItem): MutableMap<String, Any> {
        var map = mutableMapOf<String, Any>()

        if (true) {
            map["toDoId"] = firestoreToDoItem.toDoId
        }
        if (true) {
            map["toDoTitle"] = firestoreToDoItem.toDoTitle
        }
        if (true) {
            map["toDoDescription"] = firestoreToDoItem.toDoDescription
        }
        if (true) {
            map["toDoIsDone"] = firestoreToDoItem.toDoIsDone
        }
        if (true) {
            map["toDoIsFavourite"] = firestoreToDoItem.toDoIsFavourite
        }
        if (true) {
            map["toDoDoUntil"] = firestoreToDoItem.toDoDoUntil
        }
        if (true) {
            map["toDoGroupId"] = defaultGroupID
        }
        if (true) {
            map["user"] = ""
        }
        return map
    }

    suspend private fun insertToDoGroup(
        collection: String,
        vararg firestoreToDoGroups: firestoreToDoGroup
    ) {
        val targetCollection = Firebase.firestore.collection(collection)

        for (firestoreToDoGroup in firestoreToDoGroups) {
            targetCollection.add(firestoreToDoGroup)
        }
    }
}