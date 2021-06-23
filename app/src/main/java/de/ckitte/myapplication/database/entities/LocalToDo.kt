package de.ckitte.myapplication.database.entities

import androidx.room.*
import java.time.LocalDateTime

@Entity(
    tableName = "ToDo",
    indices = arrayOf(
        Index(
            value = ["toDo_Id"],
            unique = true
        )
    )
)
data class LocalToDo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "toDo_Id")
    var toDoLocalId: Int,
    @ColumnInfo(name = "toDo_RemoteId")
    var toDoRemoteId: String,
    @ColumnInfo(name = "toDo_Title")
    var toDoLocalTitle: String,
    @ColumnInfo(name = "toDo_Description")
    var toDoLocalDescription: String,
    @ColumnInfo(name = "toDo_IsDone")
    var toDoLocalIsDone: Boolean,
    @ColumnInfo(name = "toDo_IsFavourite")
    var toDoLocalIsFavourite: Boolean,
    @ColumnInfo(name = "toDo_DoUntil")
    var toDoLocalDoUntil: LocalDateTime
)
