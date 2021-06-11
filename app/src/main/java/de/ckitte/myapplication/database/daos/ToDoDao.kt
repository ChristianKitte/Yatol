package de.ckitte.myapplication.database.daos

import androidx.room.*
import de.ckitte.myapplication.database.entities.ToDoContact
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.entities.ToDoGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow as StateFlow1
import kotlin.collections.List as List

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
    suspend fun addToDoContacts(vararg toDoContacts: ToDoContact)

    @Update
    suspend fun updateToDoContacts(vararg toDoContacts: ToDoContact)

    @Delete
    suspend fun deleteToDoContacts(vararg toDoContacts: ToDoContact)

    // Abfragen

    @Query("Update ToDo set toDo_RemoteId = :remoteid where toDo_Id = :id")
    suspend fun updateRemoteToDoItemId(remoteid: String, id: Long)

    @Query("Update ToDo_Group set toDoGroup_RemoteId = :remoteid where toDoGroup_Id = :id")
    suspend fun updateRemoteToDoGroupId(remoteid: String, id: Long)

    @Query("delete from ToDo where toDo_Id = :toDoId")
    suspend fun deleteToDo(toDoId: Int)

    @Query("delete from ToDo_Group")
    suspend fun deleteAllToDoGroups()

    @Query("delete from ToDo_Contact")
    suspend fun deleteAllToDoContacts()

    @Query("select count(toDo_Id) from todo")
    suspend fun getLokalToDosCount(): Long

    // Abfragen für Flow und Observer

    // Für die Verwendung mit Flow und zur Nutzung mit einem Observer
    // ist dies Pattern notwendig. ACHTUNG: fun ohne suspend!
    // https://stackoverflow.com/questions/59170415/coroutine-flow-not-sure-how-to-convert-a-cursor-to-this-methods-return-type

    // Sortierung: Erledigt (1) am Ende ==> asc
    // Sortierung: Datum aufsteigend ==> asc
    // Sortierung: Important (1) am Anfang ==> desc
    @Query("select * from todo order by toDo_IsDone asc, toDo_DoUntil asc, toDo_IsFavourite desc")
    fun getAllToDosAsFlow_DateThenImportance(): Flow<List<ToDoItem>>

    // Sortierung: Erledigt (1) am Ende ==> asc
    // Sortierung: Important (1) am Anfang ==> desc
    // Sortierung: Datum aufsteigend ==> asc
    @Query("select * from todo order by toDo_IsDone asc, toDo_IsFavourite desc, toDo_DoUntil asc")
    fun getAllToDosAsFlow_ImportanceThenDate(): Flow<List<ToDoItem>>
}