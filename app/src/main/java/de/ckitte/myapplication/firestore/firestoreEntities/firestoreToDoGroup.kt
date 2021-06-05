package de.ckitte.myapplication.firestore.firestoreEntities

data class firestoreToDoGroup(
    var toDoGroupId: String,
    val toDoGroupIsDefault: Boolean,
    val toDoGroupTitle: String,
    val toDoGroupDescription: String,
    val user: String
)
