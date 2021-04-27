package de.ckitte.myapplication.database.relationships

import androidx.room.Embedded
import androidx.room.Relation
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.entities.ToDoGroup

data class ToDoGroupWithToDos (
    @Embedded val toDoGroup: ToDoGroup,
    @Relation(
        parentColumn = "toDoGroupId",
        entityColumn = "toDoId"
    )
    val toDos: List<ToDo>
)