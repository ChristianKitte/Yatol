package de.ckitte.myapplication.main

import android.app.Application
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.repository.ToDoRepository

class ToDoApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { ToDoDatabase.getInstance(this) }
    val repository by lazy { database.toToDao }
}