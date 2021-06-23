package de.ckitte.myapplication.firestore.firestoreEntities

data class RemoteToDoContact(
    var toDoRemoteId: String = "",
    var toDoContactRemoteID: String = "",
    val toDoRemoteUri: String = ""
)
