package de.ckitte.myapplication.firestore.firestoreEntities

/**
 * Repräsentiert eine Remote Kontakt
 * @property toDoRemoteId String Die Remote ID des zugehörigen ToDoItems
 * @property toDoContactRemoteID String Die Remote ID des Kontaktes
 * @property toDoRemoteUri String Die URI des Kontaktes auf dem erzeugenden Mobil Phone
 * @constructor
 */
data class RemoteToDoContact(
    var toDoRemoteId: String = "",
    var toDoContactRemoteID: String = "",
    val toDoRemoteUri: String = ""
)
