package de.ckitte.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.main.ToDoApplication
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val db: ToDoDao = ToDoDatabase.getInstance(this).toToDao

        GlobalScope.launch {
            ToDoRepository(db).emptyDatabase()
            ToDoRepository(db).ensureDefaultGroup()
            ToDoRepository(db).createSampleEntities()
        }
    }
    https://developer.android.com/codelabs/android-room-with-a-view-kotlin#12
    https://github.com/googlecodelabs/android-room-with-a-view/blob/kotlin/app/src/main/java/com/example/android/roomwordssample/WordsApplication.kt
    https://developer.android.com/training/dependency-injection
    https://medium.com/swlh/create-recyclerview-in-android-fragment-c0f0b151125f

}