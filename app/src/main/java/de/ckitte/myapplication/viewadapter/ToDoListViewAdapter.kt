package de.ckitte.myapplication.viewadapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.ckitte.myapplication.model.ToDoListModel
import de.ckitte.myapplication.R
import de.ckitte.myapplication.util.DateTimeUtil.Companion.getTimeString
import de.ckitte.myapplication.database.entities.LocalToDo
import de.ckitte.myapplication.databinding.FragmentTodoListitemBinding
import de.ckitte.myapplication.viewadapter.ToDoListViewAdapter.ToDoViewHolder
import java.time.LocalDateTime

/**
 *
 * @property viewModel ToDoListModel
 * @constructor
 */
class ToDoListViewAdapter(private val viewModel: ToDoListModel) :
    ListAdapter<LocalToDo, ToDoViewHolder>(ToDoComparator()) {

    /**
     *
     * @param parent ViewGroup
     * @param viewType Int
     * @return ToDoViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding =
            FragmentTodoListitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ToDoViewHolder(binding, viewModel)
    }

    /**
     *
     * @param holder ToDoViewHolder
     * @param position Int
     */
    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    /**
     *
     * @property binding FragmentTodoListitemBinding
     * @property viewModel ToDoListModel
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
     *
     */
    class ToDoComparator : DiffUtil.ItemCallback<LocalToDo>() {
        /**
         *
         * @param oldItemLokal LocalToDo
         * @param newItemLokal LocalToDo
         * @return Boolean
         */
        override fun areItemsTheSame(oldItemLokal: LocalToDo, newItemLokal: LocalToDo): Boolean {
            return oldItemLokal === newItemLokal
        }

        /**
         *
         * @param oldItemLokal LocalToDo
         * @param newItemLokal LocalToDo
         * @return Boolean
         */
        override fun areContentsTheSame(oldItemLokal: LocalToDo, newItemLokal: LocalToDo): Boolean {
            return oldItemLokal.toDoLocalId == newItemLokal.toDoLocalId
        }
    }
}