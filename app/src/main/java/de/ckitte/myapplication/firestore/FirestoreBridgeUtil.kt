package de.ckitte.myapplication.firestore

import de.ckitte.myapplication.database.entities.LocalToDoContact
import de.ckitte.myapplication.database.entities.LocalToDo
import de.ckitte.myapplication.firestore.firestoreEntities.RemoteToDoContact
import de.ckitte.myapplication.firestore.firestoreEntities.RemoteToDo

/**
 * Stellt statische Methoden zur Verfügung, um ein Remote Element auf Basis eines lokalen Elements zu erzeugen
 */
class FirestoreBridgeUtil {
    companion object {
        /***
         * Erzeugt ein Remote ToDoItem auf Basis eines lokalen ToDoItems
         * @param lokalToDo LocalToDo Das lokale [LocalToDo] Element
         * @return RemoteToDo Das entsprechende [RemoteToDo] Remote Element
         */
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

        /**
         * Erzeugt ein Remote ToDoContact auf Basis eines lokalen ToDoContacts
         * @param localToDoContact LocalToDoContact Das lokale [LocalToDoContact] Element
         * @return RemoteToDoContact Das entsprechende [RemoteToDoContact] Remote Element
         */
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