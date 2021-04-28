package de.ckitte.myapplication.database.daos

import androidx.room.*
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.entities.ToDoGroup

@Dao
interface ToDoDao {
    @Insert
    suspend fun addUser(vararg toDos: ToDo)

    @Update
    suspend fun updateUser(vararg toDos: ToDo)

    @Delete
    suspend fun deleteUser(vararg toDos: ToDo)

    @Insert
    suspend fun addGroup(vararg toDoGroups: ToDoGroup)

    @Update
    suspend fun updateGroup(vararg toDoGroups: ToDoGroup)

    @Delete
    suspend fun deleteGroup(vararg toDoGroups: ToDoGroup)

    @Query("select * from todo")
    suspend fun getAllToDos(): List<ToDo>
}