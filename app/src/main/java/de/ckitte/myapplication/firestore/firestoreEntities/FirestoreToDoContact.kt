package de.ckitte.myapplication.firestore.firestoreEntities

data class FirestoreToDoContact(
    val toDoId: String,
    var toDoContactID: String,
    val toDoHostID: String,
    val user: String
)
