package de.ckitte.myapplication.database.relationships

import androidx.room.Embedded
import androidx.room.Relation
import de.ckitte.myapplication.database.entities.LokalToDoContact
import de.ckitte.myapplication.database.entities.LokalToDo

data class ToDosItemsWithContacts(
    @Embedded val lokalToDo: LokalToDo,
    @Relation(
        parentColumn = "toDo_Id",
        entityColumn = "toDo_Id"
    )
    val toDoContacts: List<LokalToDoContact>
)