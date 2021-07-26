## Yatol
Bei YATOL (“Yet another ToDo List”) handelt es sich um eine einfache ToDo Liste für Android auf Basis von SQLite und Firestore. Es handelt es sich 
hierbei um meine allererste Anwendung für Android und Firebase.

Die Anwendung wurde mit Kotlin in der Version 1.5.10 gegen API Level 29 programmiert. Als Datenbank kamen auf Seite des Smartphones SQLite, 
serverseitig Firebase Firestore zum Einsatz. Als Abstraktionsframework für SQLite wurde ROOM in der Version 2.3 verwendet, zur Authentifizierung 
Firebase Auth.

Für die Navigation kamen die Navigation Components (Android Jetpack) zum Einsatz. Weiter wurde als Ersatz für als deprecated vermerkte Kotlin 
Extension Pakete für die einfache Kopplung von Ressourcen und Code das neue View Binding (Android Jetpack) verwendet.

Eine kompilierte Version kann [**hier**](https://github.com/ChristianKitte/Yatol/tree/master/app/release) herunter geladen werden. Hierfür
muss die Installation aus unbekannter Quelle erlaubt sein. Aus Gründen der Sicherheit, habe ich die Ressourcen auf Seiten von Firebase 
umbenannt. Die Anwendung lässt sich jedoch auch so starten. Hierzu ist temporär die Onlineverbindung zu deaktivieren. In diesem Fall 
startet die Anwendung direkt und nutzt eine lokale SQLite Datenbank.

#### Die Ordnerstruktur erklärt sich wie folgt:

- database ⇒ SQLite relevanter Code und Datenbankdefinition
- converters ⇒ aktuell lediglich ein Konverter für DateTime
- daos ⇒ aktuell nur ein Interface für ROOM, welche den data access layer definiert
- entities ⇒ die Datenbank Entitäten
- relationships ⇒ Definition der Datenbankrelationen (ToDo zu Kontakt)
- firestore ⇒ Firestore relevanter Code
- firestoreEntities ⇒ die Datenbank Entitäten auf Seiten von Firestore
- login ⇒ lediglich ein Wrapper für die Firestore Funktionalitäten
- model ⇒ view models für die ToDoListe und dem Editbereich
- repository ⇒ zentraler Zugriff auf die Daten auf einer logischen Ebene (also eine Kapselung der eigentlichen Datenbanken)
- startup ⇒  zentrale Activities
- surface ⇒  restlicher Code für die Hauptansichten (Fragments)
- util ⇒ diverse Hilfsklassen, Enumerationen usw.
- viewadapter ⇒ zwei Adapter, einmal für die ToDos und einmal für die Kontakte

Der Zugriff auf die ToDos und zugeordneten Kontakte erfolgt mit Flow. Aktualisierungen werden so fast vollständig automatisiert zur Anzeige gebracht. Bei Änderung der Sortierung erfolgt hierbei ein Switch des Flows (MutableStateFlow).

Durch die Verwendung der Navigation Komponenten baut sich meine Anwendung visuell so auf, dass die MainActivity nur ein NavHostFragment enthält, die eigentlichen Oberflächen auf Basis von Fragment existieren. Die Navigation lässt sich aus der Ressource navigation_main.xml ersehen.

Für die Interaktion verfügt die Anwendung über ein OptionMenü, sowie eine zentrale Action Bar am Fuß der Anwendung. Hier ist die Funktionalität für Sortierung, Refresh, und Hinzufügen untergebracht. Wird die Anwendung geschlossen, so wird der Nutzer explizit auch remote ausgeloggt.

Für die Netzkonnektivität verfügt die Anwendung über einen Observer (in NetworkUtil). Dieser ermöglicht die Reaktion auf sich ändernde Netzverfügbarkeiten. Diese werden mit dem LogIn Zustand im Kopf der App angezeigt.

Das Löschen von ToDos oder Kontakten erfolgt mit einer Wischgeste nach links. Hier erfolgt jeweils eine Rückfrage mit der Möglichkeit zum Abbruch. Die Kontakte eines ToDos können via eMail, SMS oder Call kontaktiert werden.

#### Die Zustände “Wichtig”, “Erledigt” und "Überfällig" werden durch farbige Unterlegungen hervorgehoben:

- Default ⇒ Hellgrün
- Default und Wichtig ⇒ Dunkelgrün
- Erledigt ⇒ Hellgrau
- Erledigt und Erledigt ⇒ Dunkelgrau
- Verspätet ⇒ Hellorange
- Verspätet und Wichtig ⇒ Dunkelorange
