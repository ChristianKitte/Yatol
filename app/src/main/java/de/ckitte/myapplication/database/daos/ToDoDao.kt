package de.ckitte.myapplication.database.daos

import androidx.room.*
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.entities.ToDoGroup
import kotlinx.coroutines.flow.Flow
import kotlin.collections.List as List

@Dao
interface ToDoDao {
    @Insert
    suspend fun addToDo(vararg toDos: ToDo)

    @Update
    suspend fun updateToDo(vararg toDos: ToDo)

    @Delete
    suspend fun deleteToDo(vararg toDos: ToDo)

    @Insert
    suspend fun addGroup(toDoGroup: ToDoGroup): Long

    @Insert
    suspend fun addGroup(vararg toDoGroup: ToDoGroup)

    @Update
    suspend fun updateGroup(vararg toDoGroups: ToDoGroup)

    @Delete
    suspend fun deleteGroup(vararg toDoGroups: ToDoGroup)

    @Query("delete from ToDo")
    suspend fun deleteAllToDos()

    @Query("delete from ToDo_Group")
    suspend fun deleteAllToDoGroups()

    // Flow from kotlinx-coroutines...
    /*
    @Query("select * from todo")
    fun getAllToDos(): List<ToDo>
    */

    @Query("select * from todo")
    fun getAllToDos(): Flow<List<ToDo>>

}