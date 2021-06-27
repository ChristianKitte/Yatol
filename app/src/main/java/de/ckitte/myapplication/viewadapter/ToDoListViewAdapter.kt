package de.ckitte.myapplication.viewadapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.entities.LocalToDo
import de.ckitte.myapplication.databinding.FragmentTodoListitemBinding
import de.ckitte.myapplication.model.ToDoListModel
import de.ckitte.myapplication.util.DateTimeUtil.Companion.getTimeString
import de.ckitte.myapplication.viewadapter.ToDoListViewAdapter.ToDoViewHolder
import java.time.LocalDateTime

/**
 * ListView Adapter für die Anzeige von ToDoItems
 * @property viewModel ToDoListModel Das zugehörige ViewModel
 * @constructor
 */
class ToDoListViewAdapter(private val viewModel: ToDoListModel) :
    ListAdapter<LocalToDo, ToDoViewHolder>(ToDoComparator()) {

    /**
     * Wird bei der Erzeugung eines neuen ViewHolders aufgerufen
     * @param parent ViewGroup Die übergebene ViewGruppe
     * @param viewType Int Der übergebene ViewTyp
     * @return ToDoViewHolder Eine neue Instanz von [ToDoViewHolder]
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding =
            FragmentTodoListitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ToDoViewHolder(binding, viewModel)
    }

    /**
     * Wird bei der Bindung von Daten und ViewHolder aufgerufen
     * @param holder ContactViewHolder Der übergebene View Holder vom Typ [ToDoViewHolder]
     * @param position Int Die Position des zu bindenden Elements
     */
    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    /**
     * Der [ToDoViewHolder] des Adapters. Implementiert die Logik der visuelle Darstellung des ToDos auf Basis
     * der hinterlegten Ressource
     * @property binding FragmentTodoListitemBinding Ein Binding Objekt der zu verwendenen Ressource
     * @property viewModel ToDoListModel Das zu verwendende ViewModel
     * @constructor
     */
    class ToDoViewHolder(
        private val binding: FragmentTodoListitemBinding,
        private var viewModel: ToDoListModel
    ) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         *
         * @param localToDo LocalToDo
         */
        @SuppressLint("ResourceAsColor")
        fun bind(localToDo: LocalToDo) {
            binding.apply {
                tvTitle.text = localToDo.toDoLocalTitle

                tvDescription.text = localToDo.toDoLocalDescription

                checkIsDone.isChecked = localToDo.toDoLocalIsDone
                checkIsFavourite.isChecked = localToDo.toDoLocalIsFavourite

                tvDoUntil.text = getTimeString(localToDo.toDoLocalDoUntil)

                btnEdit.setOnClickListener {
                    viewModel.setCurrentToDoItem(localToDo)
                    it.findNavController().navigate(R.id.action_toDoList_to_editToDo)
                }

                checkIsDone.setOnClickListener {
                    localToDo.toDoLocalIsDone = checkIsDone.isChecked
                    viewModel.updateToDoItem(localToDo)
                }

                checkIsFavourite.setOnClickListener {
                    localToDo.toDoLocalIsFavourite = checkIsFavourite.isChecked
                    viewModel.updateToDoItem(localToDo)
                }

                // Vorbelegung
                if (localToDo.toDoLocalIsFavourite) {
                    statusBar.setBackgroundResource(R.color.isStartFavourite)
                } else {
                    statusBar.setBackgroundResource(R.color.isStart)
                }

                if (localToDo.toDoLocalIsDone) {
                    // erledigt...
                    if (localToDo.toDoLocalIsFavourite) {
                        statusBar.setBackgroundResource(R.color.isDoneFavourite)
                    } else {
                        statusBar.setBackgroundResource(R.color.isDone)
                    }
                } else if (!localToDo.toDoLocalIsDone && localToDo.toDoLocalDoUntil < LocalDateTime.now()) {
                    //nicht erledigt und zu spät...
                    if (localToDo.toDoLocalIsFavourite) {
                        statusBar.setBackgroundResource(R.color.isLateFavourite)
                    } else {
                        statusBar.setBackgroundResource(R.color.isLate)
                    }
                }
            }
        }
    }

    /**
     * Ein Vergleichsobjekt für [LocalToDo]
     */
    class ToDoComparator : DiffUtil.ItemCallback<LocalToDo>() {
        /**
         * Zwei Elemente sind gleich, wenn deren Instanzen gleich sind
         * @param oldItemLokal LocalToDo Das alte Element
         * @param newItemLokal LocalToDo Das neue Element
         * @return Boolean True, wenn sie identisch sind, sonst False
         */
        override fun areItemsTheSame(oldItemLokal: LocalToDo, newItemLokal: LocalToDo): Boolean {
            return oldItemLokal === newItemLokal
        }

        /**
         * Zwei Elemente sind identisch, wenn deren lokale ID übereinstimmen
         * @param oldItemLokal LocalToDo Das alte Element
         * @param newItemLokal LocalToDo Das neue Element
         * @return Boolean True, wenn sie gleich sind, sonst False
         */
        override fun areContentsTheSame(oldItemLokal: LocalToDo, newItemLokal: LocalToDo): Boolean {
            return oldItemLokal.toDoLocalId == newItemLokal.toDoLocalId
        }
    }
}