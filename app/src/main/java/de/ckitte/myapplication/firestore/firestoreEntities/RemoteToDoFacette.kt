package de.ckitte.myapplication.firestore.firestoreEntities

import java.time.LocalDateTime

data class RemoteToDoFacette(
    var toDoRemoteId: String = "",
    var toDoRemoteTitle: String = "",
    var toDoRemoteDescription: String = "",
    var toDoRemoteIsDone: String = "",
    var toDoRemoteIsFavourite: String = "",
    var toDoRemoteDoUntil:HashMap<String,Any> = HashMap<String,Any>(),
    val toDoRemoteUser: String = "",
)