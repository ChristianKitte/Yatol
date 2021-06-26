package de.ckitte.myapplication.database.daos

import androidx.room.*
import de.ckitte.myapplication.database.entities.LocalToDoContact
import de.ckitte.myapplication.database.entities.LocalToDo
import de.ckitte.myapplication.database.relationships.ToDosItemsWithContacts
import de.ckitte.myapplication.util.ToDoContactState
import kotlinx.coroutines.flow.Flow

/**
 * Die DAO Klasse der ROOM Bibliothek. Enthält Zugriffe zur SQLite DB via Room
 */
@Dao
interface ToDoDao {
    //region CRUD ToDoItem

    /**
     * Fügt das übergebene ToDoItem in die Datenbank ein
     * @param lokalToDo LocalToDo Das hinzuzufügende Element
     * @return Long Gibt die ID des hinzugefügten Objektes zurück
     */
    @Insert
    suspend fun addLocalToDo(lokalToDo: LocalToDo): Long

    /**
     * Aktualisiert das übergebene ToDoItem in der lokalen Datenbank
     * @param lokalToDos LocalToDo Das zu aktualisierende Element
     */
    @Update
    suspend fun updateLokalToDo(lokalToDos: LocalToDo)

    /**
     * Löscht das übergebene ToDoItem aus der lokalen Datenbank
     * @param lokalToDo LocalToDo Das zu löschende Element
     */
    @Delete
    suspend fun deleteLokalToDo(lokalToDo: LocalToDo)

    //endregion

    //region CRUD ToDoContacts

    /**
     * Fügt den übergebenen ToDoKontakt in die Datenbank ein
     * @param toDoContact LocalToDoContact Das hinzuzufügende Element
     * @return Long Gibt die ID des hinzugefügten Objektes zurück
     */
    @Insert
    suspend fun addLocalToDoContact(toDoContact: LocalToDoContact): Long

    /**
     * Aktualisiert den übergebenen ToDoKontakt in der lokalen Datenbank
     * @param toDoContact LocalToDoContact Das zu aktualisierende Element
     */
    @Update
    suspend fun updateLocalToDoContact(toDoContact: LocalToDoContact)

    /**
     * Löscht den übergebenen ToDoKontakt aus der lokalen Datenbank
     * @param toDoContact LocalToDoContact Das zu löschende Element
     */
    @Delete
    suspend fun deleteLocalToDoContact(toDoContact: LocalToDoContact)

    //endregion

    //region Delete Abfragen

    /**
     * Löscht alle lokalen ToDos
     */
    @Query("delete from ToDo")
    suspend fun deleteAllLocalToDos()

    /**
     * Löscht alle lokalen Kontakte
     */
    @Query("delete from ToDo_Contact")
    suspend fun deleteAllLocalToDoContacts()

    /**
     * Löscht alle lokalen Kontakte eines ToDoItems
     * @param toDoId Int Die ID des zugehörigen ToDoItems
     */
    @Query("delete from ToDo where toDo_Id = :toDoId")
    suspend fun deleteLokalToDoById(toDoId: Int)

    //endregion

    //region Update Abfragen

    /**
     * Aktualisiert ein ToDoItem mit seiner Remote ID
     * @param remoteid String Die Remote ID
     * @param id Long Die ID des ToDos
     */
    @Query("Update ToDo set toDo_RemoteId = :remoteid where toDo_Id = :id")
    suspend fun updateRemoteToDoItemId(remoteid: String, id: Long)

    /**
     * Aktualisiert einen Kontakt mit seiner Remote ID
     * @param remoteid String Die Remote ID
     * @param id Long Die ID des Kontaktes
     */
    @Query("Update ToDo_Contact set toDoContact_RemoteId = :remoteid where toDoContact_Id = :id")
    suspend fun updateRemoteToDoContactId(remoteid: String, id: Long)

    //endregion

    //region Get Abfragen

    /**
     * Gibt die Anzahl der lokalen ToDos zurück
     * @return Long Die Anzahl der Einträge
     */
    @Query("select count(toDo_Id) from ToDo")
    suspend fun getLocalToDosCount(): Long

    /**
     * Gibt ein einzelnes lokales ToDoItem wieder
     * @param toDoId Int Die ID des ToDos
     * @return List<LocalToDo> Das lokale ToDoItem in einer Liste
     */
    @Query("select * from ToDo where toDo_Id = :toDoId")
    suspend fun getLocalToDoById(toDoId: Int): List<LocalToDo>

    /**
     * Gibt alle erledigte ToDos wieder
     * @return List<LocalToDo> Die lokale ToDos in einer Liste
     */
    @Query("select * from ToDo where toDo_IsDone=1")
    suspend fun getLocalToDoByDone(): List<LocalToDo>

    /**
     * Gibt alle lokale ToDos wieder
     * @return List<LocalToDo> Die lokale ToDos in einer Liste
     */
    @Query("select * from ToDo")
    suspend fun getAllLocalToDos(): List<LocalToDo>

    /*
    /**
     * Gibt alle als gelöscht vermerkte lokale Kontakte zurück
     * @param action Int  Der abzurufende Status Deleted
     * @return List<LocalToDoContact> Die lokalen Kontakte in einer Liste
     */
    @Query("select * from ToDo_Contact where toDoContact_State = :action")
    suspend fun getAllLocalDeletedToDoContacts(action: Int = ToDoContactState.Deleted.ordinal): List<LocalToDoContact>

    /**
     * Gibt alle als hinzugefügt vermerkte lokale Kontakte zurück
     * @param action Int Der abzurufende Status ADDED
     * @return List<LocalToDoContact> Die lokalen Kontakte in einer Liste
     */
    @Query("select * from ToDo_Contact where toDoContact_State = :action")
    suspend fun getAllLocalAddedToDoContacts(action: Int = ToDoContactState.Added.ordinal): List<LocalToDoContact>

    /**
     * Gibt alle als gespeichert vermerkte lokale Kontakte zurück
     * @param action Int Der abzurufende Status SAVE
     * @return List<LocalToDoContact> Die lokalen Kontakte in einer Liste
     */
    @Query("select * from ToDo_Contact where toDoContact_State != :action")
    suspend fun getAllLocalTouchedToDoContacts(action: Int = ToDoContactState.Save.ordinal): List<LocalToDoContact>
    */

    /**
     * Gibt alle lokale Kontakte mit dem angegebenen Status zurück
     * @param action Int Der abzurufende Status als [ToDoContactState]
     * @return List<LocalToDoContact> Die lokalen Kontakte in einer Liste
     */
    @Query("select * from ToDo_Contact where toDoContact_State = :action")
    suspend fun getAllLocalToDoContactsByState(action: Int): List<LocalToDoContact>

    /**
     * Gibt alle lokale Kontakte, die nicht dem angegebenen Status entsprechen, zurück
     * @param action Int Der abzurufende Status als [ToDoContactState]
     * @return List<LocalToDoContact> Die lokalen Kontakte in einer Liste
     */
    @Query("select * from ToDo_Contact where toDoContact_State != :action")
    suspend fun getAllLocalToDoContactsByInverseState(action: Int): List<LocalToDoContact>

    /**
     * Gibt alle Kontakte eines ToDoItems zurück
     * @param toDoItemID Long Die ID des lokalen ToDoItems
     * @return List<LocalToDoContact> Die lokalen Kontakte in einer Liste
     */
    @Query("select * from ToDo_Contact where toDo_Id = :toDoItemID")
    suspend fun getAllLocalToDoContactsByToDo(
        toDoItemID: Long
    ): List<LocalToDoContact>

    /**
     * Gibt das durch die ID bezeichneten lokale ToDoItem mit dessen ungefilterten Kontakte zurück.
     * Hierbei handelt es sich nicht um eine eingebettete Liste, sondern um zwei Collections.
     * @param toDoItemID Long Die ID des lokalen ToDoItems
     * @return List<LocalToDoContact> Eine Liste aller ToDoItems und deren Kontakte
     */
    @Transaction
    @Query("select * from ToDo where (toDo_Id = :toDoItemID)")
    fun ToDosItemsWithContacts(toDoItemID: Long): List<ToDosItemsWithContacts>

    //endregion

    //region Abfragen für Flow und Observer

    // Für die Verwendung mit Flow und zur Nutzung mit einem Observer ist dies Pattern notwendig.
    // ACHTUNG: fun ohne suspend!
    // https://stackoverflow.com/questions/59170415/coroutine-flow-not-sure-how-to-convert-a-cursor-to-this-methods-return-type

    /**
     * Alle lokalen ToDos als sortierte Liste
     *
     * Sortierung: Erledigt (1) am Ende ==> asc
     *
     * Sortierung: Datum aufsteigend ==> asc
     *
     * Sortierung: Important (1) am Anfang ==> desc

     * @return Flow<List<LocalToDo>> Das angeforderte Flow Objekt
     */
    @Query("select * from ToDo order by toDo_IsDone asc, toDo_DoUntil asc, toDo_IsFavourite desc")
    fun getAllLocalToDosAsFlowByDateThenImportance(): Flow<List<LocalToDo>>


    /**
     * Alle lokalen ToDos als sortierte Liste
     *
     * Sortierung: Erledigt (1) am Ende ==> asc
     *
     * Sortierung: Important (1) am Anfang ==> desc
     *
     * Sortierung: Datum aufsteigend ==> asc
     * @return Flow<List<LocalToDo>> Das angeforderte Flow Objekt
     */
    @Query("select * from ToDo order by toDo_IsDone asc, toDo_IsFavourite desc, toDo_DoUntil asc")
    fun getAllLocalToDosAsFlowByImportanceThenDate(): Flow<List<LocalToDo>>

    // Es scheint zu einem Problem zu kommen, wenn ich keine Klammern verwende ==> Abstürze !

    /*
    /**
     * Alle lokalen Kontakte des angegebenen ToDos, sofern sie einen Status ungleich des übergebenen
     * haben
     * @param toDoItemID Long Die ID des ToDos
     * @param action Int Der nicht zu berücksichtigende Status der Kontakte
     * @return Flow<List<LocalToDoContact>> Das angeforderte Flow Objekt
     */
    @Query("select * from ToDo_Contact where (toDo_Id = :toDoItemID) AND (toDoContact_State <> :action)")
    fun getAllLocalValidToDoContactsByToDo(
        toDoItemID: Long,
        action: Int = ToDoContactState.Deleted.ordinal
    ): Flow<List<LocalToDoContact>>
    */

    /**
     * Alle lokalen Kontakte des angegebenen ToDos, sofern sie einen Status ungleich des übergebenen
     * haben
     * @param toDoItemID Long Die ID des ToDos
     * @param action Int Der nicht zu berücksichtigende Status der Kontakte
     * @return Flow<List<LocalToDoContact>> Das angeforderte Flow Objekt
     */
    @Query("select * from ToDo_Contact where (toDo_Id = :toDoItemID) AND (toDoContact_State <> :action)")
    fun getAllLocalValidToDoContactsByToDo(
        toDoItemID: Long, action: Int
    ): Flow<List<LocalToDoContact>>

    //endregion
}