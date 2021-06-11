package de.ckitte.myapplication.firestore

import de.ckitte.myapplication.database.entities.ToDoContact
import de.ckitte.myapplication.database.entities.ToDoGroup
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.firestore.firestoreEntities.FirestoreToDoContact
import de.ckitte.myapplication.firestore.firestoreEntities.FirestoreToDoGroup
import de.ckitte.myapplication.firestore.firestoreEntities.FirestoreToDoItem

class FirestoreBridgeUtil {
    companion object {
        fun getFirestoreItemFromDatabaseItem(toDoItem: ToDoItem): FirestoreToDoItem {

            val firestoreToDoItem = FirestoreToDoItem(
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

        fun getFirestoreItemFromDatabaseItem(toDoGroup: ToDoGroup): FirestoreToDoGroup {

            val firestoreToDoGroup = FirestoreToDoGroup(
                toDoGroupId = toDoGroup.toDoGroupRemoteId,
                toDoGroupIsDefault = toDoGroup.toDoGroupIsDefault,
                toDoGroupTitle = toDoGroup.toDoGroupTitle,
                toDoGroupDescription = toDoGroup.toDoGroupDescription,
                user = "Nutzer"
            )

            return firestoreToDoGroup
        }

        fun getFirestoreItemFromDatabaseItem(toDoContact: ToDoContact): FirestoreToDoContact {

            val firestoreToDoContacts = FirestoreToDoContact(
                toDoId = toDoContact.toDoItemRemoteId,
                toDoContactID = toDoContact.toDoContactRemoteId,
                toDoHostID = toDoContact.toDoContactHostId,
                user = "Nutzer"
            )

            return firestoreToDoContacts
        }
    }
}