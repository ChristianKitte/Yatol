package de.ckitte.myapplication.firestore.firestoreEntities

/**
 * Repräsentiert ein Remote ToDoItem
 * @property toDoRemoteId String Die Remote ID des Eintrages
 * @property toDoRemoteTitle String Die Titel des Eintrages
 * @property toDoRemoteDescription String Die Beschreibung des Eintrages
 * @property toDoRemoteIsDone Boolean True (1), wenn der Eintrag erledigt ist
 * @property toDoRemoteIsFavourite Boolean True (1), wenn der Eintrag wichtig ist
 * @property toDoRemoteDoUntil String Das Datum der Fälligkeit
 * @property toDoRemoteUser String Der Besitzer des Eintrages (Reserve für zukünftige Erweiterung)
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