package de.ckitte.myapplication.database.dao

import androidx.room.*
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.entities.ToDoGroup

@Dao
interface ToDoDao {
    @Insert
    suspend fun addUser(toDo: ToDo)

    @Update
    suspend fun updateUser(toDo: ToDo)

    @Delete
    suspend fun deleteUser(toDo: ToDo)

    @Insert
    suspend fun addGroup(toDoGroup: ToDoGroup)

    @Update
    suspend fun updateGroup(toDoGroup: ToDoGroup)

    @Delete
    suspend fun deleteGroup(toDoGroup: ToDoGroup)

    @Query("select * from todo")
    suspend fun getAllToDos(): List<ToDo>
}