package de.ckitte.myapplication.database.entities

import androidx.room.*

@Entity(
    tableName = "ToDo_Contact",
    indices = arrayOf(
        Index(
            value = ["toDoContact_Id"],
            unique = true
        ),
        Index(
            value = ["toDo_Id"],
            unique = false
        )
    )
)
data class LokalToDoContact(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "toDoContact_Id")
    var toDoContactLocalId: Int,
    @ColumnInfo(name = "toDoContact_RemoteId")
    var toDoContactRemoteId: String,
    @ColumnInfo(name = "toDoContact_Uri")
    var toDoContactLocalUri: String,
    @ColumnInfo(name = "toDo_Id")
    var toDoLocalId: Long,
    @ColumnInfo(name = "toDo_RemoteId")
    var toDoRemoteId: String,
    @ColumnInfo(name = "toDoContact_State")
    var toDoContactLocalState: Int
)
