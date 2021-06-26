package de.ckitte.myapplication.database.relationships

import androidx.room.Embedded
import androidx.room.Relation
import de.ckitte.myapplication.database.entities.LocalToDoContact
import de.ckitte.myapplication.database.entities.LocalToDo

/**
 *  Definiert die 1:N Relation ToDoItem zu Kontakte
 * @property lokalToDo LocalToDo Das lokale ToDoItem (Parent)
 * @property toDoContacts List<LocalToDoContact> Die zugehörigen Kontakte (Children) des ToDoItems
 */
data class ToDosItemsWithContacts(
    @Embedded val lokalToDo: LocalToDo,
    @Relation(
        parentColumn = "toDo_Id",
        entityColumn = "toDo_Id"
    )
    val toDoContacts: List<LocalToDoContact>
)