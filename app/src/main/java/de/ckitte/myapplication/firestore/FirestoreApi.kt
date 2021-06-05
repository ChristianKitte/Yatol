package de.ckitte.myapplication.firestore

import androidx.compose.runtime.snapshots.Snapshot
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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


    // CRUD ToDoItem

    suspend fun insertToDoItem(
        collection: String,
        firestoreToDoItem: firestoreToDoItem
    ): firestoreToDoItem {
        val targetCollection = Firebase.firestore.collection(collection)
        var newID: String = ""

        // soll explizit blocken !
        targetCollection.add(firestoreToDoItem).addOnSuccessListener {
            firestoreToDoItem.toDoId = it.id
        }.await()

        return firestoreToDoItem
    }

    suspend fun updateToDoItem(
        collection: String,
        firestoreToDoItem: firestoreToDoItem
    ) {
        val targetCollection = Firebase.firestore.collection(collection)

        val map = getMutableMapFromToDoItem(firestoreToDoItem)
        targetCollection.document(firestoreToDoItem.toDoId).set(map, SetOptions.merge()).await()
    }

    private fun getMutableMapFromToDoItem(firestoreToDoItem: firestoreToDoItem): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()

        map["toDoId"] = firestoreToDoItem.toDoId
        map["toDoTitle"] = firestoreToDoItem.toDoTitle
        map["toDoDescription"] = firestoreToDoItem.toDoDescription
        map["toDoIsDone"] = firestoreToDoItem.toDoIsDone
        map["toDoIsFavourite"] = firestoreToDoItem.toDoIsFavourite
        map["toDoDoUntil"] = firestoreToDoItem.toDoDoUntil
        map["toDoGroupId"] = defaultGroupID
        map["user"] = firestoreToDoItem.user

        return map
    }

    suspend fun deleteToDoItem(
        collection: String,
        firestoreToDoItem: firestoreToDoItem
    ) {
        val targetCollection = Firebase.firestore.collection(collection)
        targetCollection.document(firestoreToDoItem.toDoId).delete().await()
    }

    // CRUD ToDoGroupItem

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

    suspend fun insertToDoGroup(
        collection: String,
        firestoreToDoGroup: firestoreToDoGroup
    ): firestoreToDoGroup {
        val targetCollection = Firebase.firestore.collection(collection)
        var newID: String = ""

        // soll explizit blocken !
        targetCollection.add(firestoreToDoGroup).addOnSuccessListener {
            firestoreToDoGroup.toDoGroupId = it.id
        }.await()

        return firestoreToDoGroup
    }

    suspend fun updateToDoGroup(
        collection: String,
        firestoreToDoGroup: firestoreToDoGroup
    ) {
        val targetCollection = Firebase.firestore.collection(collection)

        val map = getMutableMapFromToDoGrup(firestoreToDoGroup)
        targetCollection.document(firestoreToDoGroup.toDoGroupId).set(map, SetOptions.merge())
            .await()
    }

    private fun getMutableMapFromToDoGrup(firestoreToDoGroup: firestoreToDoGroup): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()

        map["toDoGroupId"] = firestoreToDoGroup.toDoGroupId
        map["toDoGroupIsDefault"] = firestoreToDoGroup.toDoGroupIsDefault
        map["toDoGroupTitle"] = firestoreToDoGroup.toDoGroupTitle
        map["toDoGroupDescription"] = firestoreToDoGroup.toDoGroupDescription
        map["user"] = firestoreToDoGroup.user

        return map
    }

    suspend fun deleteToDoGroup(
        collection: String,
        firestoreToDoGroup: firestoreToDoGroup
    ) {
        val targetCollection = Firebase.firestore.collection(collection)
        targetCollection.document(firestoreToDoGroup.toDoGroupId).delete().await()
    }

    // CRUD ToDoContacts

    // Zusätzliche Funktionalität

    suspend fun emptyStore() {
        DeleteAllDocumentsFromCollection(getToDoContactCollection)
        DeleteAllDocumentsFromCollection(getToDoItemCollection)
        DeleteAllDocumentsFromCollection(getToDoGroupCollection)
    }

    suspend fun DeleteAllDocumentsFromCollection(
        collection: String
    ) {
        // Es ist kritisch und wird von Google nicht empfohlen, Sammlungen mit
        // mobilen Geräten zu lösche. Mobile Function ist leider niht in meinem
        // Tarif enthalten. Daher gehe ich vorsichtig vor und nutze explizit await()

        val targetCollection = Firebase.firestore.collection(collection)

        val snapshot: QuerySnapshot = targetCollection.get().await()

        snapshot.forEach {
            targetCollection.document(it.id).delete().await()
        }
    }

    // Synchronisierung

    suspend fun syncOnlineStoreWithLocalStore() {
        //Firestore mit lokalen Daten füllen
    }

    suspend fun synLocalStoreWithOnlineStore() {
        //SQLite mit Online Daten füllen
    }
}