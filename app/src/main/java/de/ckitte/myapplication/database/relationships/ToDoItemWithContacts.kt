package de.ckitte.myapplication.database.relationships

import androidx.room.Embedded
import androidx.room.Relation
import de.ckitte.myapplication.database.entities.LocalToDoContact
import de.ckitte.myapplication.database.entities.LocalToDo

/**
 *
 * @property lokalToDo LocalToDo
 * @property toDoContacts List<LocalToDoContact>
 * @constructor
 */
data class ToDosItemsWithContacts(
    @Embedded val lokalToDo: LocalToDo,
    @Relation(
        parentColumn = "toDo_Id",
        entityColumn = "toDo_Id"
    )
    val toDoContacts: List<LocalToDoContact>
)