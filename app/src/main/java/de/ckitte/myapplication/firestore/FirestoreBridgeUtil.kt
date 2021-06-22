package de.ckitte.myapplication.firestore

import de.ckitte.myapplication.database.entities.LokalToDoContact
import de.ckitte.myapplication.database.entities.LokalToDo
import de.ckitte.myapplication.firestore.firestoreEntities.RemoteToDoContact
import de.ckitte.myapplication.firestore.firestoreEntities.RemoteToDo

class FirestoreBridgeUtil {
    companion object {
        fun getRemoteToDoTemplateFromLokalToDo(lokalToDo: LokalToDo): RemoteToDo {

            val firestoreToDoItem = RemoteToDo(
                toDoRemoteId = lokalToDo.toDoRemoteId,
                toDoRemoteTitle = lokalToDo.toDoLocalTitle,
                toDoRemoteDescription = lokalToDo.toDoLocalDescription,
                toDoRemoteIsDone = lokalToDo.toDoLocalIsDone,
                toDoRemoteIsFavourite = lokalToDo.toDoLocalIsFavourite,
                toDoRemoteDoUntil = lokalToDo.toDoLocalDoUntil,
                toDoRemoteUser = "Nutzer"
            )

            return firestoreToDoItem
        }

        fun getRemoteToDoContactTemplateFromLokalToDoContact(localToDoContact: LokalToDoContact): RemoteToDoContact {

            val firestoreToDoContacts = RemoteToDoContact(
                toDoRemoteId = localToDoContact.toDoRemoteId,
                toDoContactRemoteID = localToDoContact.toDoContactRemoteId,
                toDoRemoteUri = localToDoContact.toDoContactLocalUri
            )

            return firestoreToDoContacts
        }
    }
}