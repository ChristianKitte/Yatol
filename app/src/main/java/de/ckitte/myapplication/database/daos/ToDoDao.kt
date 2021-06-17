package de.ckitte.myapplication.database.daos

import androidx.room.*
import de.ckitte.myapplication.database.entities.ToDoContact
import de.ckitte.myapplication.database.entities.ToDoGroup
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.util.ContactState
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {
    // CRUD ToDoItem

    @Insert
    suspend fun addToDoItem(toDo: ToDoItem): Long

    @Update
    suspend fun updateToDoItem(toDos: ToDoItem)

    @Delete
    suspend fun deleteToDoItem(toDo: ToDoItem)

    @Query("delete from ToDo")
    suspend fun deleteAllToDoItems()

    // CRUD ToDoGroupItem

    @Insert
    suspend fun addToDoGroup(toDoGroup: ToDoGroup): Long

    @Insert
    suspend fun addToDoGroup(vararg toDoGroup: ToDoGroup)

    @Update
    suspend fun updateToDoGroup(vararg toDoGroups: ToDoGroup)

    @Delete
    suspend fun deleteToDoGroup(vararg toDoGroups: ToDoGroup)

    // CRUD ToDoContacts

    @Insert
    suspend fun addToDoContact(toDoContact: ToDoContact): Long

    @Update
    suspend fun updateToDoContact(toDoContact: ToDoContact)

    @Delete
    suspend fun deleteToDoContact(toDoContact: ToDoContact)

    // Abfragen

    @Query("Update ToDo set toDo_RemoteId = :remoteid where toDo_Id = :id")
    suspend fun updateRemoteToDoItemId(remoteid: String, id: Long)

    @Query("Update ToDo_Group set toDoGroup_RemoteId = :remoteid where toDoGroup_Id = :id")
    suspend fun updateRemoteToDoGroupId(remoteid: String, id: Long)

    @Query("Update ToDo_Contact set toDoContact_RemoteId = :remoteid where toDoContact_Id = :id")
    suspend fun updateRemoteToDoContactId(remoteid: String, id: Long)

    @Query("delete from ToDo where toDo_Id = :toDoId")
    suspend fun deleteToDo(toDoId: Int)

    @Query("delete from ToDo_Group")
    suspend fun deleteAllToDoGroups()

    @Query("delete from ToDo_Contact")
    suspend fun deleteAllToDoContacts()

    @Query("select count(toDo_Id) from ToDo")
    suspend fun getLokalToDosCount(): Long

    @Query("select * from ToDo_Group")
    suspend fun getAllToDoGroups(): List<ToDoGroup>

    @Query("select * from ToDo")
    suspend fun getAllLokalToDos(): List<ToDoItem>

    @Query("select * from ToDo_Contact where toDo_Id = :toDoItemID")
    suspend fun getAllToDoContacts(toDoItemID: Long): List<ToDoContact>

    @Query("select * from ToDo_Contact where toDoContact_State = :action")
    suspend fun getAllDeletedToDoContacts(action: Int = ContactState.Deleted.ordinal): List<ToDoContact>

    @Query("select * from ToDo_Contact where toDoContact_State = :action")
    suspend fun getAllAddedToDoContacts(action: Int = ContactState.Added.ordinal): List<ToDoContact>

    @Query("select * from ToDo_Contact where toDoContact_State != :action")
    suspend fun getAllTouchedToDoContacts(action: Int = ContactState.Save.ordinal): List<ToDoContact>

    // Abfragen für Flow und Observer

    // Für die Verwendung mit Flow und zur Nutzung mit einem Observer
    // ist dies Pattern notwendig. ACHTUNG: fun ohne suspend!
    // https://stackoverflow.com/questions/59170415/coroutine-flow-not-sure-how-to-convert-a-cursor-to-this-methods-return-type

    // Sortierung: Erledigt (1) am Ende ==> asc
    // Sortierung: Datum aufsteigend ==> asc
    // Sortierung: Important (1) am Anfang ==> desc
    @Query("select * from ToDo order by toDo_IsDone asc, toDo_DoUntil asc, toDo_IsFavourite desc")
    fun getAllToDosAsFlowByDateThenImportance(): Flow<List<ToDoItem>>

    // Sortierung: Erledigt (1) am Ende ==> asc
    // Sortierung: Important (1) am Anfang ==> desc
    // Sortierung: Datum aufsteigend ==> asc
    @Query("select * from ToDo order by toDo_IsDone asc, toDo_IsFavourite desc, toDo_DoUntil asc")
    fun getAllToDosAsFlowByImportanceThenDate(): Flow<List<ToDoItem>>

    // Es scheint zu einem Problem zu kommen, wenn ich keine Klammern verwende ==> Abstürze !
    @Query("select * from ToDo_Contact where (toDo_Id = :toDoItemID) AND (toDoContact_State <> :action)")
    fun getAllContacts(
        toDoItemID: Long,
        action: Int = ContactState.Deleted.ordinal
    ): Flow<List<ToDoContact>>
}