package de.ckitte.myapplication.firestore.firestoreEntities

import java.time.LocalDateTime

data class firestoreToDoItem(
    var toDoId: String,
    var toDoTitle: String,
    var toDoDescription: String,
    var toDoIsDone: Boolean,
    var toDoIsFavourite: Boolean,
    var toDoDoUntil: LocalDateTime,
    val toDoGroupId: String,
    val user: String
)