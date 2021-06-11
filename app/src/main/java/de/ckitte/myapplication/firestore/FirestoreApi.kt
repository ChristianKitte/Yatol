package de.ckitte.myapplication.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.ckitte.myapplication.firestore.firestoreEntities.FirestoreToDoContact
import de.ckitte.myapplication.firestore.firestoreEntities.FirestoreToDoGroup
import de.ckitte.myapplication.firestore.firestoreEntities.FirestoreToDoItem
import kotlinx.coroutines.tasks.await

class FirestoreApi {
    var db = FirebaseFirestore.getInstance()

    companion object {
        val defaultGroupID: String = "default"

        val getToDoItemCollection = "ToDoItems"
        val getToDoGroupCollection = "Groups"
        val getToDoContactCollection = "Contacts"
    }

    // CRUD ToDoItem

    suspend fun insertToDoItem(
        collection: String,
        firestoreToDoItem: FirestoreToDoItem
    ): FirestoreToDoItem {
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
        firestoreToDoItem: FirestoreToDoItem
    ) {
        val targetCollection = Firebase.firestore.collection(collection)

        val map = getMutableMapFromToDoItem(firestoreToDoItem)
        targetCollection.document(firestoreToDoItem.toDoId).set(map, SetOptions.merge()).await()
    }

    private fun getMutableMapFromToDoItem(firestoreToDoItem: FirestoreToDoItem): MutableMap<String, Any> {
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
        firestoreToDoItem: FirestoreToDoItem
    ) {
        val targetCollection = Firebase.firestore.collection(collection)
        targetCollection.document(firestoreToDoItem.toDoId).delete().await()
    }

    // CRUD ToDoGroupItem

    suspend fun insertToDoGroup(
        collection: String,
        firestoreToDoGroup: FirestoreToDoGroup
    ): FirestoreToDoGroup {
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
        firestoreToDoGroup: FirestoreToDoGroup
    ) {
        val targetCollection = Firebase.firestore.collection(collection)

        val map = getMutableMapFromToDoGrup(firestoreToDoGroup)
        targetCollection.document(firestoreToDoGroup.toDoGroupId).set(map, SetOptions.merge())
            .await()
    }

    private fun getMutableMapFromToDoGrup(firestoreToDoGroup: FirestoreToDoGroup): MutableMap<String, Any> {
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
        firestoreToDoGroup: FirestoreToDoGroup
    ) {
        val targetCollection = Firebase.firestore.collection(collection)
        targetCollection.document(firestoreToDoGroup.toDoGroupId).delete().await()
    }

    // CRUD ToDoContacts

    suspend fun insertToDoContact(
        collection: String,
        firestoreToDoContact: FirestoreToDoContact
    ): FirestoreToDoContact {
        val targetCollection = Firebase.firestore.collection(collection)
        var newID: String = ""

        // soll explizit blocken !
        targetCollection.add(firestoreToDoContact).addOnSuccessListener {
            firestoreToDoContact.toDoContactID = it.id
        }.await()

        return firestoreToDoContact
    }

    suspend fun updateToDoContact(
        collection: String,
        firestoreToDoContact: FirestoreToDoContact
    ) {
        val targetCollection = Firebase.firestore.collection(collection)

        val map = getMutableMapFromToDoItemContact(firestoreToDoContact)
        targetCollection.document(firestoreToDoContact.toDoContactID).set(map, SetOptions.merge())
            .await()
    }

    private fun getMutableMapFromToDoItemContact(firestoreToDoContact: FirestoreToDoContact): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()

        map["toDoId"] = firestoreToDoContact.toDoId
        map["toDoContactID"] = firestoreToDoContact.toDoContactID
        map["toDoHostID"] = firestoreToDoContact.toDoHostID
        map["user"] = firestoreToDoContact.user

        return map
    }

    suspend fun deleteToDoContact(
        collection: String,
        firestoreToDoContact: FirestoreToDoContact
    ) {
        val targetCollection = Firebase.firestore.collection(collection)
        targetCollection.document(firestoreToDoContact.toDoContactID).delete().await()
    }

    // Zusätzliche Funktionalität

    suspend fun emptyStore() {
        DeleteAllDocumentsFromCollection(getToDoContactCollection)
        DeleteAllDocumentsFromCollection(getToDoItemCollection)
        DeleteAllDocumentsFromCollection(getToDoGroupCollection)
    }

    private suspend fun DeleteAllDocumentsFromCollection(
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