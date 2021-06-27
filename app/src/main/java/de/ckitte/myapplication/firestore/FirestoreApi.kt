package de.ckitte.myapplication.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import de.ckitte.myapplication.firestore.firestoreEntities.RemoteToDo
import de.ckitte.myapplication.firestore.firestoreEntities.RemoteToDoContact
import kotlinx.coroutines.tasks.await

/**
 * Stellt Funktionalität für den Zugriff auf eine Firestore Datenbank zur Verfügung
 * @property db FirebaseFirestore Instanz der aktuellen Firestore Datenbank
 */
class FirestoreApi {
    private val db = FirebaseFirestore.getInstance()

    companion object {
        /**
         * Die Bezeichnung der Collection für ToDoItems
         */
        val getToDoRemoteCollection = "ToDoItems"

        /**
         * Die Bezeichnung der Collection für ToDoContacts
         */
        val getToDoContactRemoteCollection = "Contacts"
    }

    //region CRUD ToDoItem

    /**
     * Fügt ein Remote ToDoItem in eine Collection ein
     * @param collection String Die Collection, in der das Objekt eingefügt werden soll
     * @param remoteToDo RemoteToDo Das einzufügende [RemoteToDo]
     * @return RemoteToDo Das hinzugefügte [RemoteToDo] Element mit seiner RemoteID
     */
    suspend fun insertToDoRemoteItem(
        collection: String,
        remoteToDo: RemoteToDo
    ): RemoteToDo {
        val targetCollection = Firebase.firestore.collection(collection)

        // soll explizit blocken !
        targetCollection.add(remoteToDo).addOnSuccessListener {
            remoteToDo.toDoRemoteId = it.id
        }.await()

        return remoteToDo
    }

    /**
     * Aktualisiert ein Remote ToDoItem in einer Collection
     * @param collection String Die Collection, in der das Objekt vorhanden ist
     * @param remoteToDo RemoteToDo Ein [RemoteToDo] mit neuen Werten
     */
    suspend fun updateToDoRemoteItem(
        collection: String,
        remoteToDo: RemoteToDo
    ) {
        val targetCollection = Firebase.firestore.collection(collection)

        val map = getMutableMapFromToDoRemoteItem(remoteToDo)
        targetCollection.document(remoteToDo.toDoRemoteId).set(map, SetOptions.merge())
            .await()
    }

    /**
     * Erzeugt eine für die Aktualisierung notwendig Map auf Basis des übergebenen Elements für alle Eigenschaften.
     * Nicht geänderte Werte behalten hierbei ihren Wert.
     * @param remoteToDo RemoteToDo Ein [RemoteToDo] mit neuen Werten
     * @return MutableMap<String, Any> Eine Map, welche zur Aktualisierung des Remote ToDos verwendet werden kann.
     */
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

    /**
     * Löscht ein Remote ToDoItem aus einer Collection
     * @param collection String Die Collection, die das Element enthält
     * @param remoteToDo RemoteToDo Das zu löschende [RemoteToDo] Element
     */
    suspend fun deleteToDoRemoteItem(
        collection: String,
        remoteToDo: RemoteToDo
    ) {
        val targetCollection = Firebase.firestore.collection(collection)
        targetCollection.document(remoteToDo.toDoRemoteId).delete().await()
    }

    //endregion

    //region CRUD ToDoContacts

    /**
     * Fügt einen Remote Kontakt in eine Collection ein
     * @param collection String Die Collection, in der das Element eingefügt werden soll
     * @param remoteToDoContact RemoteToDoContact Das hinzuzufügende [RemoteToDoContact] Element
     * @return RemoteToDoContact Das hinzugefügte [RemoteToDoContact] Element mit seiner RemoteID
     */
    suspend fun insertToDoContact(
        collection: String,
        remoteToDoContact: RemoteToDoContact
    ): RemoteToDoContact {
        val targetCollection = Firebase.firestore.collection(collection)

        // soll explizit blocken !
        targetCollection.add(remoteToDoContact).addOnSuccessListener {
            remoteToDoContact.toDoContactRemoteID = it.id
        }.await()

        return remoteToDoContact
    }

    /**
     * Aktualisiert einen Remote ToDoContact in einer Collection
     * @param collection String Die Collection, in der das Objekt vorhanden ist
     * @param remoteToDoContact RemoteToDoContact Ein [RemoteToDoContact] mit neuen Werten
     */
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

    /**
     * Erzeugt eine für die Aktualisierung notwendig Map auf Basis des übergebenen Elements für alle Eigenschaften.
     * Nicht geänderte Werte behalten hierbei ihren Wert.
     * @param remoteToDoContact RemoteToDoContact Ein [RemoteToDoContact] mit neuen Werten
     * @return MutableMap<String, Any> Eine Map, welche zur Aktualisierung des Remote ToDoContacts verwendet werden kann.
     */
    private fun getMutableMapFromToDoItemContact(remoteToDoContact: RemoteToDoContact): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()

        map["toDoRemoteId"] = remoteToDoContact.toDoRemoteId
        map["toDoContactRemoteID"] = remoteToDoContact.toDoContactRemoteID
        map["toDoLocalUri"] = remoteToDoContact.toDoRemoteUri

        return map
    }

    /**
     * Löscht ein Remote ToDoItem aus einer Collection
     * @param collection String Die Collection, die das Element enthält
     * @param remoteToDoContact RemoteToDoContact Das zu löschende [RemoteToDoContact] Element
     */
    suspend fun deleteToDoContact(
        collection: String,
        remoteToDoContact: RemoteToDoContact
    ) {
        val targetCollection = Firebase.firestore.collection(collection)
        targetCollection.document(remoteToDoContact.toDoContactRemoteID).delete().await()
    }

    //endregion

    //region Zusätzliche Funktionalität

    /**
     * Gibt alle remote verfügbaren ToDos zurück
     * @return List<RemoteToDo> Eine Liste aller [RemoteToDo]
     */
    suspend fun getAllRemoteToDos(): List<RemoteToDo> {
        var remoteToDo: List<RemoteToDo> = emptyList()

        try {
            val targetCollection = Firebase.firestore.collection(getToDoRemoteCollection)
            val querySnapshot = targetCollection.get().await()

            querySnapshot.documents.forEach {

                val newToDo = it.toObject<RemoteToDo>()

                if (newToDo != null) {
                    newToDo.toDoRemoteId = it.id // ==> in ?.let{} ist id nicht verfügbar....
                    remoteToDo = remoteToDo.plus(newToDo)
                }
            }

            return remoteToDo
        } catch (e: Exception) {
            remoteToDo = emptyList()
            return remoteToDo
        }
    }

    /**
     * Gibt alle remote verfügbaren Kontakte eines ToDos zurück
     * @param toDoRemoteId String Die RemoteID des ToDos
     * @return List<RemoteToDoContact> Eine Liste aller für den Kontakt verfügbaren [RemoteToDoContact]
     */
    suspend fun getAllRemoteToDoContactsByRemoteToDo(toDoRemoteId: String): List<RemoteToDoContact> {
        var remoteToDoContacts: List<RemoteToDoContact> = emptyList()

        try {
            val targetCollection = Firebase.firestore.collection(getToDoContactRemoteCollection)
            val querySnapshot =
                targetCollection.whereEqualTo("toDoRemoteId", toDoRemoteId).get().await()

            querySnapshot.documents.forEach {
                val newContact = it.toObject<RemoteToDoContact>()

                if (newContact != null) {
                    newContact.toDoContactRemoteID =
                        it.id // ==> in ?.let{} ist id nicht verfügbar....
                    remoteToDoContacts = remoteToDoContacts.plus(newContact)
                }
            }

            return remoteToDoContacts
        } catch (e: Exception) {
            remoteToDoContacts = emptyList()
            return remoteToDoContacts
        }
    }

    /**
     * Löschte alle Remote verfügbaren Items
     */
    suspend fun deleteAllRemoteItems() {
        DeleteAllRemoteDocumentsFromCollection(getToDoContactRemoteCollection)
        DeleteAllRemoteDocumentsFromCollection(getToDoRemoteCollection)
    }

    /**
     * Löscht alle Documents einer Collection
     * @param collection String Die Collection, deren Documents gelöscht werden sollen
     */
    private suspend fun DeleteAllRemoteDocumentsFromCollection(
        collection: String
    ) {
        // Es ist kritisch und wird von Google nicht empfohlen, Sammlungen mit
        // mobilen Geräten zu löschen. Mobile Function ist leider nicht in meinem
        // Tarif enthalten. Daher gehe ich vorsichtig vor und nutze explizit await()

        val targetCollection = Firebase.firestore.collection(collection)

        val snapshot: QuerySnapshot = targetCollection.get().await()

        snapshot.forEach {
            targetCollection.document(it.id).delete().await()
        }
    }

    //endregion
}