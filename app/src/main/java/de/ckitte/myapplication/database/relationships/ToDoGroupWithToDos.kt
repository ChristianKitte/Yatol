package de.ckitte.myapplication.database.relationships

import androidx.room.Embedded
import androidx.room.Relation
import de.ckitte.myapplication.database.entities.ToDoContact
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.entities.ToDoGroup

/*
 Gruppen sind vorbereitet, werden aber aktuell in dieser Version
 noch nicht unterstützt wegen Detailfragen

data class ToDoGroupWithToDos(
    @Embedded val toDoGroup: ToDoGroup,
    @Relation(
        parentColumn = "toDoGroup_Id",
        entityColumn = "toDoGroup_Id"
    )
    val toDos: List<ToDoItem>
)


data class ToDosWithToDoContacts(
    @Embedded val toDoItem: ToDoItem,
    @Relation(
        parentColumn = "toDo_Id",
        entityColumn = "toDo_Id"
    )
    val toDoContacts: List<ToDoContact>
)*/