package de.ckitte.myapplication.database.entities

import androidx.room.*
import java.time.LocalDateTime

/**
 * Repräsentiert ein lokales ToDoItem
 * @property toDoLocalId Int Die lokale ID des Eintrages
 * @property toDoRemoteId String Die Remote ID des Eintrages
 * @property toDoLocalTitle String Der Titel des Eintrages
 * @property toDoLocalDescription String Die Beschreibung des Eintrages
 * @property toDoLocalIsDone Boolean True (1), wenn der Eintrag erledigt ist
 * @property toDoLocalIsFavourite Boolean True (1), wenn der Eintrag wichtig ist
 * @property toDoLocalDoUntil LocalDateTime, Das Datum der Fälligkeit
 * @constructor
 */
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
