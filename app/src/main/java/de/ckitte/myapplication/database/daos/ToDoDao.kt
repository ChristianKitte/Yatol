package de.ckitte.myapplication.database.daos

import androidx.room.*
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.entities.ToDoGroup
import kotlinx.coroutines.flow.Flow
import kotlin.collections.List as List

@Dao
interface ToDoDao {
    // CRUD ToDoItem

    @Insert
    suspend fun addToDoItem(vararg toDos: ToDoItem)

    @Update
    suspend fun updateToDoItem(vararg toDos: ToDoItem)

    @Delete
    suspend fun deleteToDoItem(vararg toDos: ToDoItem)

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

    // Abfragen

    @Query("delete from ToDo where toDo_Id = :toDoId")
    suspend fun deleteToDo(toDoId: Int)

    @Query("delete from ToDo_Group")
    suspend fun deleteAllToDoGroups()

    // Flow und Observer

    // Für die Verwendung mit Flow und zur Nutzung mit einem Observer
    // ist dies Pattern notwendig. ACHTUNG: fun ohne suspend!
    @Query("select * from todo order by toDo_IsDone, toDo_DoUntil, toDo_IsFavourite asc")
    fun getAllToDosAsFlow_DateThenImportance(): Flow<List<ToDoItem>>

    @Query("select * from todo order by toDo_IsDone, toDo_IsFavourite, toDo_DoUntil asc")
    fun getAllToDosAsFlow_ImportanceThenDate(): Flow<List<ToDoItem>>
}