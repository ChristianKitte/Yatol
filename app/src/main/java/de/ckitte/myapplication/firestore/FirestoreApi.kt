package de.ckitte.myapplication.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.ckitte.myapplication.firestore.firestoreEntities.RemoteToDoContact
import de.ckitte.myapplication.firestore.firestoreEntities.RemoteToDo
import kotlinx.coroutines.tasks.await

class FirestoreApi {
    var db = FirebaseFirestore.getInstance()

    companion object {
        val getToDoRemoteCollection = "ToDoItems"
        val getToDoContactRemoteCollection = "Contacts"
    }

    // CRUD ToDoItem

    suspend fun insertToDoRemoteItem(
        collection: String,
        remoteToDo: RemoteToDo
    ): RemoteToDo {
        val targetCollection = Firebase.firestore.collection(collection)
        var newID: String = ""

        // soll explizit blocken !
        targetCollection.add(remoteToDo).addOnSuccessListener {
            remoteToDo.toDoRemoteId = it.id
        }.await()

        return remoteToDo
    }

    suspend fun updateToDoRemoteItem(
        collection: String,
        remoteToDo: RemoteToDo
    ) {
        val targetCollection = Firebase.firestore.collection(collection)

        val map = getMutableMapFromToDoRemoteItem(remoteToDo)
        targetCollection.document(remoteToDo.toDoRemoteId).set(map, SetOptions.merge())
            .await()
    }

    private fun getMutableMapFromToDoRemoteItem(remoteToDo: RemoteToDo): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()

        map["toDoRemoteId"] = remoteToDo.toDoRemoteId
        map["toDoRemoteTitle"] = remoteToDo.toDoRemoteTitle
        map["toDoRemoteDescription"] = remoteToDo.toDoRemoteDescription
        map["toDoRemoteIsDone"] = remoteToDo.toDoRemoteIsDone
        map["toDoRemoteIsFavourite"] = remoteToDo.toDoRemoteIsFavourite
        map["toDoRemoteDoUntil"] = remoteToDo.toDoRemoteDoUntil
        map["toDoRemoteUser"] = remoteToDo.toDoRemoteUser

        return map
    }

    suspend fun deleteToDoRemoteItem(
        collection: String,
        remoteToDo: RemoteToDo
    ) {
        val targetCollection = Firebase.firestore.collection(collection)
        targetCollection.document(remoteToDo.toDoRemoteId).delete().await()
    }

    // CRUD ToDoContacts

    suspend fun insertToDoContact(
        collection: String,
        remoteToDoContact: RemoteToDoContact
    ): RemoteToDoContact {
        val targetCollection = Firebase.firestore.collection(collection)
        var newID: String = ""

        // soll explizit blocken !
        targetCollection.add(remoteToDoContact).addOnSuccessListener {
            remoteToDoContact.toDoContactRemoteID = it.id
        }.await()

        return remoteToDoContact
    }

    suspend fun updateToDoContact(
        collection: String,
        remoteToDoContact: RemoteToDoContact
    ) {
        val targetCollection = Firebase.firestore.collection(collection)

        val map = getMutableMapFromToDoItemContact(remoteToDoContact)
        targetCollection.document(remoteToDoContact.toDoContactRemoteID)
            .set(map, SetOptions.merge())
            .await()
    }

    private fun getMutableMapFromToDoItemContact(remoteToDoContact: RemoteToDoContact): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()

        map["toDoRemoteId"] = remoteToDoContact.toDoRemoteId
        map["toDoContactRemoteID"] = remoteToDoContact.toDoContactRemoteID
        map["toDoLocalUri"] = remoteToDoContact.toDoRemoteUri

        return map
    }

    suspend fun deleteToDoContact(
        collection: String,
        remoteToDoContact: RemoteToDoContact
    ) {
        val targetCollection = Firebase.firestore.collection(collection)
        targetCollection.document(remoteToDoContact.toDoContactRemoteID).delete().await()
    }

    // Zusätzliche Funktionalität

    suspend fun deleteAllRemoteItems() {
        DeleteAllRemoteDocumentsFromCollection(getToDoContactRemoteCollection)
        DeleteAllRemoteDocumentsFromCollection(getToDoRemoteCollection)
    }

    private suspend fun DeleteAllRemoteDocumentsFromCollection(
        collection: String
    ) {
        // Es ist kritisch und wird von Google nicht empfohlen, Sammlungen mit
        // mobilen Geräten zu lösche. Mobile Function ist leider nicht in meinem
        // Tarif enthalten. Daher gehe ich vorsichtig vor und nutze explizit await()

        val targetCollection = Firebase.firestore.collection(collection)

        val snapshot: QuerySnapshot = targetCollection.get().await()

        snapshot.forEach {
            targetCollection.document(it.id).delete().await()
        }
    }
}