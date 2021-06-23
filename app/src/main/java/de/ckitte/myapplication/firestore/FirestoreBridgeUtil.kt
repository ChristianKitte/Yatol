package de.ckitte.myapplication.firestore

import de.ckitte.myapplication.database.entities.LocalToDoContact
import de.ckitte.myapplication.database.entities.LocalToDo
import de.ckitte.myapplication.firestore.firestoreEntities.RemoteToDoContact
import de.ckitte.myapplication.firestore.firestoreEntities.RemoteToDo

class FirestoreBridgeUtil {
    companion object {
        fun getRemoteToDoTemplateFromLokalToDo(lokalToDo: LocalToDo): RemoteToDo {

            val firestoreToDoItem = RemoteToDo(
                toDoRemoteId = lokalToDo.toDoRemoteId,
                toDoRemoteTitle = lokalToDo.toDoLocalTitle,
                toDoRemoteDescription = lokalToDo.toDoLocalDescription,
                toDoRemoteIsDone = lokalToDo.toDoLocalIsDone,
                toDoRemoteIsFavourite = lokalToDo.toDoLocalIsFavourite,
                toDoRemoteDoUntil = lokalToDo.toDoLocalDoUntil.toString(),
                toDoRemoteUser = "Nutzer"
            )

            return firestoreToDoItem
        }

        fun getRemoteToDoContactTemplateFromLokalToDoContact(localToDoContact: LocalToDoContact): RemoteToDoContact {

            val firestoreToDoContacts = RemoteToDoContact(
                toDoRemoteId = localToDoContact.toDoRemoteId,
                toDoContactRemoteID = localToDoContact.toDoContactRemoteId,
                toDoRemoteUri = localToDoContact.toDoContactLocalUri
            )

            return firestoreToDoContacts
        }
    }
}