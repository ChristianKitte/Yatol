package de.ckitte.myapplication.database.entities

import androidx.room.*

/**
 * Repräsentiert einen lokalen Kontakt
 * @property toDoContactLocalId Int Die lokale ID des Kontaktes
 * @property toDoContactRemoteId String Die Remote ID des Kontaktes
 * @property toDoContactLocalUri String Die URI des Kontaktes auf dem erzeugenden Mobil Phone
 * @property toDoLocalId Long Die Lokale ID des zugehörigen ToDos
 * @property toDoRemoteId String Die Remote ID des zugehörigen ToDos
 * @property toDoContactLocalState Int Der Status des Kontaktes als [ToDoContactState]
 * @constructor
 */
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
data class LocalToDoContact(
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
