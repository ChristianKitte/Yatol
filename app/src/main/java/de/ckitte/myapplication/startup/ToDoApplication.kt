package de.ckitte.myapplication.startup

import android.app.Application
import de.ckitte.myapplication.database.ToDoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Im Manifest hinterlegte Applikationsklasse der Anwendung
 */
class ToDoApplication : Application()
