package de.ckitte.myapplication.firestore

import de.ckitte.myapplication.database.entities.ToDoContact
import de.ckitte.myapplication.database.entities.ToDoGroup
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.firestore.firestoreEntities.firestoreToDoContact
import de.ckitte.myapplication.firestore.firestoreEntities.firestoreToDoGroup
import de.ckitte.myapplication.firestore.firestoreEntities.firestoreToDoItem

class FirestoreBridgeUtil {
    companion object {
        fun getFirestoreItemFromDatabaseItem(toDoItem: ToDoItem): firestoreToDoItem {

            val firestoreToDoItem = firestoreToDoItem(
                toDoId = toDoItem.toDoRemoteId,
                toDoTitle = toDoItem.toDoTitle,
                toDoDescription = toDoItem.toDoDescription,
                toDoIsDone = toDoItem.toDoIsDone,
                toDoIsFavourite = toDoItem.toDoIsFavourite,
                toDoDoUntil = toDoItem.toDoDoUntil,
                toDoGroupId = FirestoreApi.defaultGroupID,
                user = "Nutzer"
            )

            return firestoreToDoItem
        }

        fun getFirestoreItemFromDatabaseItem(toDoGroup: ToDoGroup): firestoreToDoGroup {

            val firestoreToDoGroup = firestoreToDoGroup(
                toDoGroupId = toDoGroup.toDoGroupRemoteId,
                toDoGroupIsDefault = toDoGroup.toDoGroupIsDefault,
                toDoGroupTitle = toDoGroup.toDoGroupTitle,
                toDoGroupDescription = toDoGroup.toDoGroupDescription,
                user = "Nutzer"
            )

            return firestoreToDoGroup
        }

        fun getFirestoreItemFromDatabaseItem(toDoContact: ToDoContact): firestoreToDoContact {

            val firestoreToDoContacts = firestoreToDoContact(
                toDoId = toDoContact.toDoItemRemoteId,
                toDoContactID = toDoContact.toDoContactRemoteId,
                toDoHostID = toDoContact.toDoContactHostId,
                user = "Nutzer"
            )

            return firestoreToDoContacts
        }
    }
}