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
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 *
 * @property toDoDao ToDoDao
 * @constructor
 */
class ToDoRepository(private val toDoDao: ToDoDao) {
    companion object StaticMembers {
        /**
         *
         */
        @Volatile
        private var currentLokalToDo: LocalToDo? = null

        /**
         *
         * @param currentLokalToDo LocalToDo
         */
        fun setCurrentToDoItem(currentLokalToDo: LocalToDo) {
            this.currentLokalToDo = currentLokalToDo
        }

        /**
         *
         * @return LocalToDo?
         */
        fun getCurrentToDoItem(): LocalToDo? {
            return this.currentLokalToDo

        }

        /**
         *
         * @return LocalToDo
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
         *
         * @return LocalToDoContact
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
    }

    //region CRUD ToDoItem

    /**
     *
     * @param lokalToDos Array<out LocalToDo>
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

                toDoDao.updateRemoteToDoItemId(insertedFirestoreToDoItem.toDoRemoteId, newID)
            }
        }
    }

    /**
     *
     * @param lokalToDos Array<out LocalToDo>
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
                }
            }
        }
    }

    /**
     *
     * @param lokalToDos Array<out LocalToDo>
     */
    @WorkerThread
    suspend fun deleteToDoItem(vararg lokalToDos: LocalToDo) {
        val api = FirestoreApi()
        setCurrentToDoItem(getNewToDoItem())

        lokalToDos.forEach {
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

    //endregion

    //region CRUD ToDoContact

    /**
     *
     * @param toDoContacts Array<out LocalToDoContact>
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
     *
     * @param toDoContacts Array<out LocalToDoContact>
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
     *
     * @param localToDoContacts Array<out LocalToDoContact>
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
     *
     */
    @WorkerThread
    suspend fun commitTransientToDoContacts() {
        val contactsToDelete = toDoDao.getAllLocalDeletedToDoContacts()
        val contactsToAdd = toDoDao.getAllLocalAddedToDoContacts()

        contactsToDelete.forEach {
            deleteToDoContacts(it)
        }

        contactsToAdd.forEach {
            it.toDoContactLocalState = ToDoContactState.Save.ordinal
            updateToDoContact(it)
        }
    }

    /**
     *
     */
    @WorkerThread
    suspend fun rollbackTransientToDoContacts() {
        val contactsTouched = toDoDao.getAllLocalTouchedToDoContacts()

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
    //Es existieren zwei Pattern. Hier: fun ohne suspend!

    /**
     *
     * @return Flow<List<LocalToDo>>
     */
    fun getAllToDosAsFlow_DateThenImportance(): Flow<List<LocalToDo>> =
        toDoDao.getAllLocalToDosAsFlowByDateThenImportance()

    /**
     *
     * @return Flow<List<LocalToDo>>
     */
    fun getAllToDosAsFlow_ImportanceThenDate(): Flow<List<LocalToDo>> =
        toDoDao.getAllLocalToDosAsFlowByImportanceThenDate()

    /**
     *
     * @param toDoItemID Long
     * @return Flow<List<LocalToDoContact>>
     */
    fun getAllContacts(toDoItemID: Long): Flow<List<LocalToDoContact>> =
        toDoDao.getAllLocalValidToDoContactsByToDo(toDoItemID)

    //endregion

    //region Synchronization of local and remote data store

    /**
     *
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
     *
     */
    @WorkerThread
    suspend fun mirrorSampleToLocal() {
        RepositoryHelper(toDoDao).createSampleEntities()
    }

    //Transaction? In Scope?
    /**
     *
     */
    @WorkerThread
    suspend fun mirrorLocalToRemote() {
        if (ConnectionLiveData.isConnected) {
            val api = FirestoreApi()

            emptyRemoteDatabase()

            val localToDos = toDoDao.getAllLocalToDos()
            localToDos.forEach { it1 ->
                // wird bei Update übertragen, da keine Remote ID und erhält seine RemoteID
                it1.toDoRemoteId = ""
                updateToDoItem(it1)

                // aktualisiertes ToDoItem undd alle Kontakte des aktuelle ToDos abfragen
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

    //Transaction? In Scope?
    //Niemals LocalDateTime mit Firestore verwenden, immer auf Text gehen !!!!!!
    /**
     *
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
     *
     */
    @WorkerThread
    suspend fun emptyLokalDatabase() {
        toDoDao.deleteAllLocalToDos()
        toDoDao.deleteAllLocalToDoContacts()
    }

    /**
     *
     */
    @WorkerThread
    suspend fun emptyRemoteDatabase() {
        val api = FirestoreApi()
        api.deleteAllRemoteItems()
    }

    //endregion
}