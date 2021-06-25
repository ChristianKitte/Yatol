package de.ckitte.myapplication.firestore.firestoreEntities

/**
 *
 * @property toDoRemoteId String
 * @property toDoContactRemoteID String
 * @property toDoRemoteUri String
 * @constructor
 */
data class RemoteToDoContact(
    var toDoRemoteId: String = "",
    var toDoContactRemoteID: String = "",
    val toDoRemoteUri: String = ""
)
