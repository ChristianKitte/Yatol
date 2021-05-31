package de.ckitte.myapplication.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.ckitte.myapplication.firestore.firestoreEntities.firestoreToDoItem


class FirestoreApi {
    var db = FirebaseFirestore.getInstance()
    private lateinit var defaultGroup: String

    suspend fun emptyStore() {
        //den lokalen Store leeren
    }

    suspend fun syncOnlineStore() {
        //Firestore mit lokalen Daten füllen
    }

    suspend fun synLocalStore() {
        //SQLite mit Online Daten füllen
    }

    suspend fun ensureDefaultGroup() {

    }

    suspend fun deleteToDoItem(collection: String, vararg firestoreToDoItems: firestoreToDoItem) {
        val toDoCollection = Firebase.firestore.collection(collection)

        for (firestoreToDoItem in firestoreToDoItems) {
            toDoCollection.document(firestoreToDoItem.toDoId).delete()
        }
    }

    suspend fun insertToDoItem(collection: String, vararg firestoreToDoItems: firestoreToDoItem) {
        val toDoCollection = Firebase.firestore.collection(collection)

        for (firestoreToDoItem in firestoreToDoItems) {
            toDoCollection.add(firestoreToDoItem)
        }
    }

    suspend fun updateToDoItem() {

    }
}