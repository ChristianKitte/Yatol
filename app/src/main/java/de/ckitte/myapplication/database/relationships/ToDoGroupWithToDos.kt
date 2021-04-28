package de.ckitte.myapplication.database.relationships

import androidx.room.Embedded
import androidx.room.Relation
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.entities.ToDoGroup

data class ToDoGroupWithToDos (
    @Embedded val toDoGroup: ToDoGroup,
    @Relation(
        parentColumn = "toDoGroup_Id",
        entityColumn = "toDoGroup_Id"
    )
    val toDos: List<ToDo>
)