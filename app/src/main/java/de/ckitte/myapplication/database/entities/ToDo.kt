package de.ckitte.myapplication.database.entities

import androidx.room.*
import java.sql.Date
import java.time.LocalDateTime

@Entity(
    tableName = "ToDo",
    foreignKeys = [ForeignKey(
        entity = ToDoGroup::class,
        parentColumns = arrayOf("toDoGroup_Id"),
        childColumns = arrayOf("toDoGroup_Id"),
        onDelete = ForeignKey.CASCADE
    )]
)
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
    val toDoDoUntil: LocalDateTime,
    @ColumnInfo(name = "toDoGroup_Id")
    val toDoGroupId: Int
)
