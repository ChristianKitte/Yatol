package de.ckitte.myapplication.database.daos

import androidx.room.*
import de.ckitte.myapplication.database.entities.LocalToDoContact
import de.ckitte.myapplication.database.entities.LocalToDo
import de.ckitte.myapplication.util.ToDoContactState
import kotlinx.coroutines.flow.Flow

/**
 *
 */
@Dao
interface ToDoDao {
    // CRUD ToDoItem

    /**
     *
     * @param lokalToDo LocalToDo
     * @return Long
     */
    @Insert
    suspend fun addLocalToDo(lokalToDo: LocalToDo): Long

    /**
     *
     * @param lokalToDos LocalToDo
     */
    @Update
    suspend fun updateLokalToDo(lokalToDos: LocalToDo)

    /**
     *
     * @param lokalToDo LocalToDo
     */
    @Delete
    suspend fun deleteLokalToDo(lokalToDo: LocalToDo)

    // CRUD ToDoContacts

    /**
     *
     * @param toDoContact LocalToDoContact
     * @return Long
     */
    @Insert
    suspend fun addLocalToDoContact(toDoContact: LocalToDoContact): Long

    /**
     *
     * @param toDoContact LocalToDoContact
     */
    @Update
    suspend fun updateLocalToDoContact(toDoContact: LocalToDoContact)

    /**
     *
     * @param toDoContact LocalToDoContact
     */
    @Delete
    suspend fun deleteLocalToDoContact(toDoContact: LocalToDoContact)

    // Abfragen

    /**
     *
     */
    @Query("delete from ToDo")
    suspend fun deleteAllLocalToDos()

    /**
     *
     */
    @Query("delete from ToDo_Contact")
    suspend fun deleteAllLocalToDoContacts()

    /**
     *
     * @param toDoId Int
     */
    @Query("delete from ToDo where toDo_Id = :toDoId")
    suspend fun deleteLokalToDoById(toDoId: Int)

    /**
     *
     * @param remoteid String
     * @param id Long
     */
    @Query("Update ToDo set toDo_RemoteId = :remoteid where toDo_Id = :id")
    suspend fun updateRemoteToDoItemId(remoteid: String, id: Long)

    /**
     *
     * @param remoteid String
     * @param id Long
     */
    @Query("Update ToDo_Contact set toDoContact_RemoteId = :remoteid where toDoContact_Id = :id")
    suspend fun updateRemoteToDoContactId(remoteid: String, id: Long)

    /**
     *
     * @return Long
     */
    @Query("select count(toDo_Id) from ToDo")
    suspend fun getLocalToDosCount(): Long

    /**
     *
     * @param toDoId Int
     * @return List<LocalToDo>
     */
    @Query("select * from ToDo where toDo_Id = :toDoId")
    suspend fun getLocalToDoById(toDoId: Int): List<LocalToDo>

    /**
     *
     * @return List<LocalToDo>
     */
    @Query("select * from ToDo")
    suspend fun getAllLocalToDos(): List<LocalToDo>

    /**
     *
     * @param action Int
     * @return List<LocalToDoContact>
     */
    @Query("select * from ToDo_Contact where toDoContact_State = :action")
    suspend fun getAllLocalDeletedToDoContacts(action: Int = ToDoContactState.Deleted.ordinal): List<LocalToDoContact>

    /**
     *
     * @param action Int
     * @return List<LocalToDoContact>
     */
    @Query("select * from ToDo_Contact where toDoContact_State = :action")
    suspend fun getAllLocalAddedToDoContacts(action: Int = ToDoContactState.Added.ordinal): List<LocalToDoContact>

    /**
     *
     * @param action Int
     * @return List<LocalToDoContact>
     */
    @Query("select * from ToDo_Contact where toDoContact_State != :action")
    suspend fun getAllLocalTouchedToDoContacts(action: Int = ToDoContactState.Save.ordinal): List<LocalToDoContact>

    /**
     *
     * @param toDoItemID Long
     * @return List<LocalToDoContact>
     */
    @Query("select * from ToDo_Contact where toDo_Id = :toDoItemID")
    suspend fun getAllLocalToDoContactsByToDo(
        toDoItemID: Long
    ): List<LocalToDoContact>

    // Abfragen für Flow und Observer

    // Für die Verwendung mit Flow und zur Nutzung mit einem Observer
    // ist dies Pattern notwendig. ACHTUNG: fun ohne suspend!
    // https://stackoverflow.com/questions/59170415/coroutine-flow-not-sure-how-to-convert-a-cursor-to-this-methods-return-type

    // Sortierung: Erledigt (1) am Ende ==> asc
    // Sortierung: Datum aufsteigend ==> asc
    // Sortierung: Important (1) am Anfang ==> desc

    /**
     *
     * @return Flow<List<LocalToDo>>
     */
    @Query("select * from ToDo order by toDo_IsDone asc, toDo_DoUntil asc, toDo_IsFavourite desc")
    fun getAllLocalToDosAsFlowByDateThenImportance(): Flow<List<LocalToDo>>

    // Sortierung: Erledigt (1) am Ende ==> asc
    // Sortierung: Important (1) am Anfang ==> desc
    // Sortierung: Datum aufsteigend ==> asc
    /**
     *
     * @return Flow<List<LocalToDo>>
     */
    @Query("select * from ToDo order by toDo_IsDone asc, toDo_IsFavourite desc, toDo_DoUntil asc")
    fun getAllLocalToDosAsFlowByImportanceThenDate(): Flow<List<LocalToDo>>

    // Es scheint zu einem Problem zu kommen, wenn ich keine Klammern verwende ==> Abstürze !
    /**
     *
     * @param toDoItemID Long
     * @param action Int
     * @return Flow<List<LocalToDoContact>>
     */
    @Query("select * from ToDo_Contact where (toDo_Id = :toDoItemID) AND (toDoContact_State <> :action)")
    fun getAllLocalValidToDoContactsByToDo(
        toDoItemID: Long,
        action: Int = ToDoContactState.Deleted.ordinal
    ): Flow<List<LocalToDoContact>>
}