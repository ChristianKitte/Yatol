package de.ckitte.myapplication.firestore.firestoreEntities

import java.time.LocalDateTime

data class RemoteToDo(
    var toDoRemoteId: String = "",
    var toDoRemoteTitle: String = "",
    var toDoRemoteDescription: String = "",
    var toDoRemoteIsDone: Boolean = false,
    var toDoRemoteIsFavourite: Boolean = false,
    var toDoRemoteDoUntil: String = "",
    val toDoRemoteUser: String = "",
)