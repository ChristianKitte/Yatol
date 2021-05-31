package de.ckitte.myapplication.startup

import android.app.Application
import de.ckitte.myapplication.database.ToDoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ToDoApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { ToDoDatabase.getInstance(this, applicationScope) }
    val repository by lazy { database.toToDao }


}