package de.ckitte.myapplication.database.daos

import androidx.room.*
import de.ckitte.myapplication.database.entities.LocalToDoContact
import de.ckitte.myapplication.database.entities.LocalToDo
import de.ckitte.myapplication.util.ToDoContactState
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {
    // CRUD ToDoItem

    @Insert
    suspend fun addLocalToDo(lokalToDo: LocalToDo): Long

    @Update
    suspend fun updateLokalToDo(lokalToDos: LocalToDo)

    @Delete
    suspend fun deleteLokalToDo(lokalToDo: LocalToDo)

    // CRUD ToDoContacts

    @Insert
    suspend fun addLocalToDoContact(toDoContact: LocalToDoContact): Long

    @Update
    suspend fun updateLocalToDoContact(toDoContact: LocalToDoContact)

    @Delete
    suspend fun deleteLocalToDoContact(toDoContact: LocalToDoContact)

    // Abfragen

    @Query("delete from ToDo")
    suspend fun deleteAllLocalToDos()

    @Query("delete from ToDo_Contact")
    suspend fun deleteAllLocalToDoContacts()

    @Query("delete from ToDo where toDo_Id = :toDoId")
    suspend fun deleteLokalToDoById(toDoId: Int)

    @Query("Update ToDo set toDo_RemoteId = :remoteid where toDo_Id = :id")
    suspend fun updateRemoteToDoItemId(remoteid: String, id: Long)

    @Query("Update ToDo_Contact set toDoContact_RemoteId = :remoteid where toDoContact_Id = :id")
    suspend fun updateRemoteToDoContactId(remoteid: String, id: Long)

    @Query("select count(toDo_Id) from ToDo")
    suspend fun getLocalToDosCount(): Long

    @Query("select * from ToDo where toDo_Id = :toDoId")
    suspend fun getLocalToDoById(toDoId: Int): List<LocalToDo>

    @Query("select * from ToDo")
    suspend fun getAllLocalToDos(): List<LocalToDo>

    @Query("select * from ToDo_Contact where toDoContact_State = :action")
    suspend fun getAllLocalDeletedToDoContacts(action: Int = ToDoContactState.Deleted.ordinal): List<LocalToDoContact>

    @Query("select * from ToDo_Contact where toDoContact_State = :action")
    suspend fun getAllLocalAddedToDoContacts(action: Int = ToDoContactState.Added.ordinal): List<LocalToDoContact>

    @Query("select * from ToDo_Contact where toDoContact_State != :action")
    suspend fun getAllLocalTouchedToDoContacts(action: Int = ToDoContactState.Save.ordinal): List<LocalToDoContact>

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
    @Query("select * from ToDo order by toDo_IsDone asc, toDo_DoUntil asc, toDo_IsFavourite desc")
    fun getAllLocalToDosAsFlowByDateThenImportance(): Flow<List<LocalToDo>>

    // Sortierung: Erledigt (1) am Ende ==> asc
    // Sortierung: Important (1) am Anfang ==> desc
    // Sortierung: Datum aufsteigend ==> asc
    @Query("select * from ToDo order by toDo_IsDone asc, toDo_IsFavourite desc, toDo_DoUntil asc")
    fun getAllLocalToDosAsFlowByImportanceThenDate(): Flow<List<LocalToDo>>

    // Es scheint zu einem Problem zu kommen, wenn ich keine Klammern verwende ==> Abstürze !
    @Query("select * from ToDo_Contact where (toDo_Id = :toDoItemID) AND (toDoContact_State <> :action)")
    fun getAllLocalValidToDoContactsByToDo(
        toDoItemID: Long,
        action: Int = ToDoContactState.Deleted.ordinal
    ): Flow<List<LocalToDoContact>>
}