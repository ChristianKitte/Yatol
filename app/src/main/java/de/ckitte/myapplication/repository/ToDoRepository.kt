package de.ckitte.myapplication.repository

import android.util.Log
import androidx.annotation.WorkerThread
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.LocalToDoContact
import de.ckitte.myapplication.database.entities.LocalToDo
import de.ckitte.myapplication.firestore.FirestoreApi
import de.ckitte.myapplication.firestore.FirestoreBridgeUtil
import de.ckitte.myapplication.util.ConnectionLiveData
import de.ckitte.myapplication.util.ToDoContactState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Repository der Anwendung. Alle Zugriffe auf und Anfragen an die Datenbanken laufen über diese Klasse.
 * Alle globalen Zustände werden hier gehalten.
 * @property toDoDao ToDoDao Die DAO Klasse der ROOM Bibliothek. Enthält Zugriffe zur SQLite DB via Room
 * @constructor
 */
class ToDoRepository(private val toDoDao: ToDoDao) {
    companion object StaticMembers {
        //region Handling der aktuellen Elemente und Templates

        /**
         * Das aktuelle [LocalToDo] Element
         */
        @Volatile
        private var currentLokalToDo: LocalToDo? = null

        /**
         * Setzt das aktuelle [LocalToDo] Element
         * @param currentLokalToDo LocalToDo
         */
        fun setCurrentToDoItem(currentLokalToDo: LocalToDo) {
            this.currentLokalToDo = currentLokalToDo
        }

        /**
         * Gibt das aktuelle [LocalToDo] Element zurück
         * @return LocalToDo?
         */
        fun getCurrentToDoItem(): LocalToDo? {
            return this.currentLokalToDo

        }

        /**
         * Eine Template für ein neues [LocalToDo] Element
         * @return LocalToDo Die Instanz eines neuen [LocalToDo] Element
         */
        fun getNewToDoItem(): LocalToDo {
            return LocalToDo(
                0,
                "",
                "",
                "",
                false,
                false,
                LocalDateTime.now()
            )
        }

        /**
         * Eine Template für ein neues [LocalToDoContact] Element
         * @return LocalToDoContact Die Instanz eines neuen [LocalToDoContact] Element
         */
        fun getNewToDoContact(): LocalToDoContact {
            return LocalToDoContact(
                toDoContactLocalId = 0,
                toDoContactRemoteId = "",
                toDoContactLocalUri = "",
                toDoLocalId = 0,
                toDoRemoteId = "",
                toDoContactLocalState = ToDoContactState.Added.ordinal
            )
        }

        //endregion
    }

    //region CRUD ToDoItem

    /**
     * Fügt die übergebenen ToDos der Datenbank hinzu
     * @param lokalToDos Array<out LocalToDo> Ein Liste mit [LocalToDo] Elemente
     */
    @WorkerThread
    suspend fun addToDoItem(vararg lokalToDos: LocalToDo) {
        val api = FirestoreApi()

        lokalToDos.forEach {
            val newID = toDoDao.addLocalToDo(it)
            it.toDoLocalId = newID.toInt()

            setCurrentToDoItem(it)

            if (ConnectionLiveData.isConnected) {
                val firestoreToDoItem = FirestoreBridgeUtil.getRemoteToDoTemplateFromLokalToDo(it)

                val insertedFirestoreToDoItem = api.insertToDoRemoteItem(
                    FirestoreApi.getToDoRemoteCollection,
                    firestoreToDoItem
                )

                toDoDao.updateRemoteToDoItemId(
                    insertedFirestoreToDoItem.toDoRemoteId,
                    newID
                )

                it.toDoRemoteId = insertedFirestoreToDoItem.toDoRemoteId
            }
        }
    }

    /**
     * Aktualisiert die übergebenen ToDos in der Datenbank
     * @param lokalToDos Array<out LocalToDo> Ein Liste mit [LocalToDo] Elemente
     */
    @WorkerThread
    suspend fun updateToDoItem(vararg lokalToDos: LocalToDo) {
        val api = FirestoreApi()

        lokalToDos.forEach {
            toDoDao.updateLokalToDo(it)
            setCurrentToDoItem(it)

            if (ConnectionLiveData.isConnected) {
                val firestoreToDoItem = FirestoreBridgeUtil.getRemoteToDoTemplateFromLokalToDo(it)

                if (it.toDoRemoteId.isNotBlank()) {
                    api.updateToDoRemoteItem(
                        FirestoreApi.getToDoRemoteCollection,
                        firestoreToDoItem
                    )
                } else if (it.toDoRemoteId.isBlank()) {
                    val insertedFirestoreToDoItem = api.insertToDoRemoteItem(
                        FirestoreApi.getToDoRemoteCollection,
                        firestoreToDoItem
                    )

                    toDoDao.updateRemoteToDoItemId(
                        insertedFirestoreToDoItem.toDoRemoteId,
                        it.toDoLocalId.toLong()
                    )
                    it.toDoRemoteId = insertedFirestoreToDoItem.toDoRemoteId
                }
            }
        }
    }

    /**
     * Löscht die übergebenen ToDos in der Datenbank
     * @param lokalToDos Array<out LocalToDo> Ein Liste mit [LocalToDo] Elemente
     */
    @WorkerThread
    suspend fun deleteToDoItem(vararg lokalToDos: LocalToDo) {
        val api = FirestoreApi()
        setCurrentToDoItem(getNewToDoItem())

        lokalToDos.forEach {
            CoroutineScope(Dispatchers.IO).launch {
                val s = toDoDao.ToDosItemsWithContacts(it.toDoLocalId.toLong())
                s.forEach {
                    it.toDoContacts.forEach {
                        deleteToDoContacts(it)
                    }
                }
            }

            toDoDao.deleteLokalToDo(it)

            if (ConnectionLiveData.isConnected && it.toDoRemoteId.isNotBlank()) {
                val firestoreToDoItem = FirestoreBridgeUtil.getRemoteToDoTemplateFromLokalToDo(it)

                api.deleteToDoRemoteItem(
                    FirestoreApi.getToDoRemoteCollection,
                    firestoreToDoItem
                )
            }
        }
    }

    /**
     * Löscht alle [LocalToDo] Elemente und deren [LocalToDoContact] Elemente, welche als erledigt gekennzeichnet wurden
     */
    suspend fun deleteDoneToDoItems() {
        toDoDao.getLocalToDoByDone().forEach {
            deleteToDoItem(it)
        }
    }

    //endregion

    //region CRUD ToDoContact

    /**
     * Fügt die übergebenen ToDoContacts in die Datenbank ein
     * @param toDoContacts Array<out LocalToDoContact> Ein Liste mit [LocalToDoContact] Elemente
     */
    @WorkerThread
    suspend fun addToDoContacts(vararg toDoContacts: LocalToDoContact) {
        val api = FirestoreApi()

        toDoContacts.forEach {
            val newID = toDoDao.addLocalToDoContact(it)
            it.toDoContactLocalId = newID.toInt()

            if (ConnectionLiveData.isConnected) {
                val firestoreToDoContact =
                    FirestoreBridgeUtil.getRemoteToDoContactTemplateFromLokalToDoContact(it)

                val insertedFirestoreToDoContact = api.insertToDoContact(
                    FirestoreApi.getToDoContactRemoteCollection,
                    firestoreToDoContact
                )

                toDoDao.updateRemoteToDoContactId(
                    insertedFirestoreToDoContact.toDoContactRemoteID,
                    it.toDoContactLocalId.toLong()
                )
            }
        }
    }

    /**
     * Aktualisiert die übergebenen ToDoContacts in der Datenbank
     * @param toDoContacts Array<out LocalToDoContact> Ein Liste mit [LocalToDoContact] Elemente
     */
    @WorkerThread
    suspend fun updateToDoContact(vararg toDoContacts: LocalToDoContact) {
        val api = FirestoreApi()

        try {
            toDoContacts.forEach {
                toDoDao.updateLocalToDoContact(it)

                if (ConnectionLiveData.isConnected) {
                    val firestoreToDoContact =
                        FirestoreBridgeUtil.getRemoteToDoContactTemplateFromLokalToDoContact(it)

                    if (it.toDoContactRemoteId.isNotBlank()) {
                        api.updateToDoContact(
                            FirestoreApi.getToDoContactRemoteCollection,
                            firestoreToDoContact
                        )
                    } else if (it.toDoContactRemoteId.isBlank()) {
                        val insertedFirestoreToDoContact = api.insertToDoContact(
                            FirestoreApi.getToDoContactRemoteCollection,
                            firestoreToDoContact
                        )

                        toDoDao.updateRemoteToDoContactId(
                            insertedFirestoreToDoContact.toDoContactRemoteID,
                            it.toDoContactLocalId.toLong()
                        )
                    }
                }
            }
        } catch (d: Exception) {
            Log.println(Log.DEBUG, "updateToDoContact", d.toString())
        }
    }

    /**
     * Löscht die übergebenen ToDoContacts in der Datenbank
     * @param localToDoContacts Array<out LocalToDoContact> Ein Liste mit [LocalToDoContact] Elemente
     */
    @WorkerThread
    suspend fun deleteToDoContacts(vararg localToDoContacts: LocalToDoContact) {
        val api = FirestoreApi()

        localToDoContacts.forEach {
            toDoDao.deleteLocalToDoContact(it)

            if (ConnectionLiveData.isConnected && it.toDoContactRemoteId.isNotBlank()) {
                val remoteToDoContact =
                    FirestoreBridgeUtil.getRemoteToDoContactTemplateFromLokalToDoContact(it)

                api.deleteToDoContact(
                    FirestoreApi.getToDoContactRemoteCollection,
                    remoteToDoContact
                )
            }
        }
    }

    //endregion

    //region Commit and rollback contacts in transient

    /**
     * Führt für alle [LocalToDoContact] im Transient ([ToDoContactState.Added] oder [ToDoContactState.Deleted])
     * einen Commit aus. Hierbei werden alle vermerkten Operationen (Hinzufügen, Löschen) durchgeführt
     * Anschließend werden deren Status auf [ToDoContactState.Save] gesetzt.
     */
    @WorkerThread
    suspend fun commitTransientToDoContacts() {
        val contactsToDelete =
            toDoDao.getAllLocalToDoContactsByState(ToDoContactState.Deleted.ordinal) //.getAllLocalDeletedToDoContacts()
        val contactsToAdd =
            toDoDao.getAllLocalToDoContactsByState(ToDoContactState.Added.ordinal) //.getAllLocalAddedToDoContacts()

        contactsToDelete.forEach {
            deleteToDoContacts(it)
        }

        contactsToAdd.forEach {
            it.toDoContactLocalState = ToDoContactState.Save.ordinal
            updateToDoContact(it)
        }
    }

    /**
     * Führt für alle [LocalToDoContact] im Transient ([ToDoContactState.Added] oder [ToDoContactState.Deleted])
     * einen Rollback aus. Anschließend werden deren Status auf [ToDoContactState.Save] gesetzt.
     */
    @WorkerThread
    suspend fun rollbackTransientToDoContacts() {
        val contactsTouched =
            toDoDao.getAllLocalToDoContactsByInverseState(ToDoContactState.Save.ordinal) //.getAllLocalTouchedToDoContacts()

        contactsTouched.forEach {
            when (it.toDoContactLocalState) {
                ToDoContactState.Added.ordinal -> {
                    deleteToDoContacts(it)
                }
                ToDoContactState.Deleted.ordinal -> {
                    it.toDoContactLocalState = ToDoContactState.Save.ordinal
                    updateToDoContact(it)
                }
            }
        }
    }

    //endregion

    //region Flow and Observer

    /**
     * Liefert ein Flowobjekt mit allen ToDos in der Sortierung Datum, dann Wichtigkeit zurück
     * @return Flow<List<LocalToDo>> Das Flowobjekt
     */
    fun getAllToDosAsFlow_DateThenImportance(): Flow<List<LocalToDo>> =
        toDoDao.getAllLocalToDosAsFlowByDateThenImportance()

    /**
     * Liefert ein Flowobjekt mit allen ToDos in der Sortierung Wichtigkeit, dann Datum zurück
     * @return Flow<List<LocalToDo>> Das Flowobjekt
     */
    fun getAllToDosAsFlow_ImportanceThenDate(): Flow<List<LocalToDo>> =
        toDoDao.getAllLocalToDosAsFlowByImportanceThenDate()

    /**
     * Liefert ein Flowobjekt mit allen Kontakten des zu der übergebenen lokalen ID gehörenden [LocalToDo]
     * Elements zurück
     * @param toDoItemID Long Die lokale ID des [LocalToDo]
     * @return Flow<List<LocalToDoContact>> Das Flowobjekt
     */
    fun getAllLocalValidToDoContactsByToDo(toDoItemID: Long): Flow<List<LocalToDoContact>> =
        toDoDao.getAllLocalValidToDoContactsByToDo(toDoItemID, ToDoContactState.Deleted.ordinal)

    //endregion

    //region Synchronisation der Datenbanken

    /**
     * Stößt die Synchronisation der Datenbanken an. Sofern die lokale Datenbank Einträge enthät, werde
     * alle Remote Daten gelöscht und die lokale Datenbank übertragen. Sofern keine lokalen Einträge vorhanden
     * sind, werden auf Basis der Remote Daten lokale Daten generiert.
     */
    @WorkerThread
    suspend fun refreshDatabase() {
        val numberOfLokalToDos = toDoDao.getLocalToDosCount()

        if (numberOfLokalToDos > 0) {
            mirrorLocalToRemote()
        } else {
            // mirrorSampleToLocal()
            mirrorRemoteToLocal()
        }
    }

    /**
     * Erzeugt Beispieldaten zu Testzwecken
     */
    @WorkerThread
    suspend fun mirrorSampleToLocal() {
        RepositoryHelper(toDoDao).createSampleEntities()
    }

    /**
     * Spiegelt die lokale Datenbank in die Remote Datenbank
     */
    @WorkerThread
    suspend fun mirrorLocalToRemote() {
        if (ConnectionLiveData.isConnected) {
            //FirestoreApi()

            emptyRemoteDatabase()

            val localToDos = toDoDao.getAllLocalToDos()
            localToDos.forEach { it1 ->
                // wird bei Update übertragen, da keine Remote ID und erhält seine RemoteID
                it1.toDoRemoteId = ""
                updateToDoItem(it1)

                // aktualisiertes ToDoItem und alle Kontakte des aktuelle ToDos abfragen
                val actCurrentToDo = toDoDao.getLocalToDoById(it1.toDoLocalId)
                val currentLocalToDoContacts =
                    toDoDao.getAllLocalToDoContactsByToDo(it1.toDoLocalId.toLong())

                // für jeden Kontakt
                currentLocalToDoContacts.forEach { it2 ->
                    // ToDoRemoteID setzen
                    // wird bei Update übertragen, da keine Remote ID und erhält seine RemoteID

                    it2.toDoContactRemoteId = ""
                    it2.toDoRemoteId = actCurrentToDo[0].toDoRemoteId

                    updateToDoContact(it2)
                }
            }
        }
    }

    /**
     * Spiegelt die Remote Datenbank in die lokale Datenbank
     */
    @WorkerThread
    suspend fun mirrorRemoteToLocal() {
        if (ConnectionLiveData.isConnected) {
            val api = FirestoreApi()

            emptyLokalDatabase()

            val remoteToDos = api.getAllRemoteToDos()
            remoteToDos.forEach { it ->
                val localToDo: LocalToDo = LocalToDo(
                    0,
                    it.toDoRemoteId,
                    it.toDoRemoteTitle,
                    it.toDoRemoteDescription,
                    it.toDoRemoteIsDone,
                    it.toDoRemoteIsFavourite,
                    LocalDateTime.parse(it.toDoRemoteDoUntil)
                )

                val newID = toDoDao.addLocalToDo(localToDo)
                val remoteToDoContacts = api.getAllRemoteToDoContactsByRemoteToDo(it.toDoRemoteId)
                remoteToDoContacts.forEach { it2 ->
                    val loacalToDoContact: LocalToDoContact = LocalToDoContact(
                        0,
                        it2.toDoContactRemoteID,
                        it2.toDoRemoteUri,
                        newID,
                        it2.toDoRemoteId,
                        ToDoContactState.Save.ordinal
                    )

                    toDoDao.addLocalToDoContact(loacalToDoContact)
                }

            }
        }
    }

    /**
     * Löscht alle Elemente der lokalen Datenbank
     */
    @WorkerThread
    suspend fun emptyLokalDatabase() {
        toDoDao.deleteAllLocalToDos()
        toDoDao.deleteAllLocalToDoContacts()
    }

    /**
     * Löscht alle Elemente der Remote Datenbank
     */
    @WorkerThread
    suspend fun emptyRemoteDatabase() {
        val api = FirestoreApi()
        api.deleteAllRemoteItems()
    }

    //endregion

    //region Sonstiges

    /**
     * Gibt die Anzahl der dem ToDoItem zugeordneten Kontakte mit der übergebenen URI zurück.
     * @param toDoID Long Die ID des lokalen ToDoItems
     * @param toDoContactUri String Die URI eines lokalen Kontaktes
     * @return Int Die Zahl an Kontakten des ToDoItems mit dieser URI
     */
    @WorkerThread
    suspend fun getLocalToDoContactsByURI(toDoID: Long, toDoContactUri: String): Int {
        return toDoDao.getLocalToDoContactsByURI(toDoID, toDoContactUri)
    }

    //endregion
}