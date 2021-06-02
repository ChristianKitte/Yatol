package de.ckitte.myapplication.database.entities

import androidx.room.*
import java.time.LocalDateTime

@Entity(
    tableName = "ToDo_Contacts",
    foreignKeys = [ForeignKey(
        entity = ToDoItem::class,
        parentColumns = arrayOf("toDo_Id"),
        childColumns = arrayOf("toDo_Id"),
        onDelete = ForeignKey.CASCADE
    )],
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
data class ToDoContacts(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "toDoContact_Id")
    val toDoContactId: Int,
    @ColumnInfo(name = "toDoContact_Remote_Id")
    val toDoContactRemoteId: String,
    @ColumnInfo(name = "toDoContact_HostId")
    var toDoContactHostId: String,
    @ColumnInfo(name = "toDo_Id")
    val toDoItemId: Long
)
