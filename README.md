## Yatol
Bei YATOL (“Yet another ToDo List”) handelt es sich um eine einfache ToDo Liste für Android auf Basis von SQLite, Firestore. Als 
Sprache kam Kotlin zum Einsatz. Zu den einzelnen ToDos lassen sich Kontakte hinzufügen und aus der Anwendung heraus kontaktieren. 

Bei der Arbeit handelt es sich um meine allererste Anwendung für Android und mein erstes Projekt mit Kotlin als Programmiersprache. Auch 
Firebase und Jetpack waren mir bis heute keine Begriffe.

Die Anwendung wurde mit Kotlin in der Version 1.5.10 gegen API Level 29 programmiert. Als Datenbank kamen auf Seite des Smartphones SQLite, 
serverseitig Firebase Firestore zum Einsatz. Als Abstraktion Framework für SQLite wurde das Framework ROOM in der Version 2.3 verwendet. 
Zur Authentifizierung wurde Firebase Auth verwendet.

Für die Navigation kamen die Navigation Components (Android Jetpack) zum Einsatz. Weiter wurde als Ersatz für als deprecated vermerkte Kotlin 
Extension Pakete für die einfache Kopplung von Ressourcen und Code das neue View Binding (Android Jetpack) verwendet.

Da vieles in Android standardisiert ist und ich meinen Code recht vollständig kommentiert habe, möchte ich hier nur auf das wichtigste zur 
Orientierung eingehen.

#### Meine Ordnerstruktur erklärt sich wie folgt:

- database ⇒ SQLite relevanter Code und Datenbankdefinition
- converters ⇒ aktuell lediglich ein Konverter für DateTime
- daos ⇒ aktuell nur ein Interface für ROOM, welche den data access layer definiert
- entities ⇒ meine Datenbank Entitäten
- relationships ⇒ Definition meiner Datenbankrelationen (ToDo zu Kontakt)
- firestore ⇒ Firestore relevanter Code
- firestoreEntities ⇒ meine Datenbank Entitäten auf Seiten von Firestore
- login ⇒ lediglich ein Wrapper für die Firestore Funktionalitäten
- model ⇒ view models für die ToDoListe und dem Editbereich
- repository ⇒ zentraler Zugriff auf die Daten auf einer logischen Ebene (also eine Kapselung der eigentlichen Datenbanken)
- startup ⇒  zentrale Activities
- surface ⇒  restlicher Code für die Hauptansichten (Fragments)
- util ⇒ diverse Hilfsklassen, Enumerationen usw.
- viewadapter ⇒ zwei Adapter, einmal für die ToDos und einmal für die Kontakte

Für das Verständnis ist es weiterhin wichtig zu wissen, dass ich ToDos und Kontakte mit Hilfe von Flows anzeige. Aktualisierungen werden so fast vollständig automatisiert zur Anzeige gebracht. Bei Änderung der Sortierung erfolgt somit ein Switch des Flows (MutableStateFlow).

Durch die Verwendung der Navigation Komponenten baut sich meine Anwendung visuell so auf, dass die MainActivity nur ein NavHostFragment enthält, die eigentlichen Oberflächen auf Basis von Fragment existieren. Die Navigation lässt sich aus der Ressource navigation_main.xml ersehen.

Für die Interaktion verfügt die Anwendung über zwei Menüs. Ein OptionMenü (sollte selbst erklären sein), sowie eine zentrale Action Bar Fuß der Anwendung. Hier ist die Funktionalität für Sortierung, Refresh, und Hinzufügen untergebracht. Wird die Anwendung geschlossen, so wird der Nutzer explizit remote ausgeloggt.

Für die Netzkonnektivität verfügt die Anwendung über einen Observer (in NetworkUtil). Dieser ermöglicht die Reaktion auf sich ändernde Netzverfügbarkeiten. Diese werden mit dem LogIn Zustand im Kopf der App angezeigt.

Das Löschen von ToDos oder Kontakten erfolgt mit einer Wischgeste nach links. Hier erfolgt jeweils eine Rückfrage mit der Möglichkeit zum Abbruch. Die Kontakte eines ToDos können via eMail, SMS oder Call kontaktiert werden.

#### Die Zustände “Wichtig”, “Erledigt” und eine Überfälligkeit hebe ich durch farbige Unterlegungen hervor:

- Default ⇒ Hellgrün
- Default und Wichtig ⇒ Dunkelgrün
- Erledigt ⇒ Hellgrau
- Erledigt und Erledigt ⇒ Dunkelgrau
- Verspätet ⇒ Hellorange
- Verspätet und Wichtig ⇒ Dunkelorange
