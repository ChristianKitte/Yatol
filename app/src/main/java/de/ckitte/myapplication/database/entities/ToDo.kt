package de.ckitte.myapplication.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "ToDo")
data class ToDo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "toDo_Id")
    val toDoId: Int,
    @ColumnInfo(name = "toDo_Title")
    val toDoTitle: String,
    @ColumnInfo(name = "toDo_Description")
    val toDoDescription: String,
    @ColumnInfo(name = "toDo_IsDone")
    val toDoIsDone: Boolean,
    @ColumnInfo(name = "toDo_IsFavourite")
    val toDoIsFavourite: Boolean,
    @ColumnInfo(name = "toDo_DoUntil")
    val toDoDoUntil: java.util.Date,
    @ColumnInfo(name = "toDoGroup_Id")
    val toDoGroupId: Int
)
