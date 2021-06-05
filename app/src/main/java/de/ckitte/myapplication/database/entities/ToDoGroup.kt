package de.ckitte.myapplication.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ToDo_Group",
    indices = arrayOf(
        Index(
            value = ["toDoGroup_Id"],
            unique = true
        )
    )
)
data class ToDoGroup(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "toDoGroup_Id")
    val toDoGroupId: Int,
    @ColumnInfo(name = "toDoGroup_RemoteId")
    val toDoGroupRemoteId: String,
    @ColumnInfo(name = "toDoGroup_IsDefault")
    val toDoGroupIsDefault: Boolean,
    @ColumnInfo(name = "toDoGroup_Title")
    val toDoGroupTitle: String,
    @ColumnInfo(name = "toDoGroup_Description")
    val toDoGroupDescription: String
)

