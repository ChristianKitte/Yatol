package de.ckitte.myapplication.firestore.firestoreEntities

import java.time.LocalDateTime

/**
 *
 * @property toDoRemoteId String
 * @property toDoRemoteTitle String
 * @property toDoRemoteDescription String
 * @property toDoRemoteIsDone Boolean
 * @property toDoRemoteIsFavourite Boolean
 * @property toDoRemoteDoUntil String
 * @property toDoRemoteUser String
 * @constructor
 */
data class RemoteToDo(
    var toDoRemoteId: String = "",
    var toDoRemoteTitle: String = "",
    var toDoRemoteDescription: String = "",
    var toDoRemoteIsDone: Boolean = false,
    var toDoRemoteIsFavourite: Boolean = false,
    var toDoRemoteDoUntil: String = "",
    val toDoRemoteUser: String = "",
)