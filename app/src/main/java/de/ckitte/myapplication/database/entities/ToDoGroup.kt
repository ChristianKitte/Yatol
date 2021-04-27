package de.ckitte.myapplication.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ToDo_Group")
data class ToDoGroup(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "toDoGroup_Id")
    val toDoGroupId: Int,
    @ColumnInfo(name = "toDoGroup_Title")
    val toDoGroupTitle: String,
    @ColumnInfo(name = "toDoGroup_Description")
    val toDoGroupDescription: String
)

