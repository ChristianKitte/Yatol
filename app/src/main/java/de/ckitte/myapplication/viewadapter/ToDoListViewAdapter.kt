package de.ckitte.myapplication.viewadapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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


class ToDoListViewAdapter(private val viewModel: ToDoListModel) :
    ListAdapter<LocalToDo, ToDoViewHolder>(ToDoComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding =
            FragmentTodoListitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding, viewModel)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class ToDoViewHolder(
        private val binding: FragmentTodoListitemBinding,
        private var viewModel: ToDoListModel
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lokalToDo: LocalToDo) {
            binding.apply {
                tvTitle.text = lokalToDo.toDoLocalTitle
                tvDescription.text = lokalToDo.toDoLocalDescription

                checkIsDone.isChecked = lokalToDo.toDoLocalIsDone
                checkIsFavourite.isChecked = lokalToDo.toDoLocalIsFavourite

                tvDoUntil.text = getTimeString(lokalToDo.toDoLocalDoUntil)
                setTitleColor(lokalToDo)
                setFavouriteIcon()

                btnEdit.setOnClickListener {
                    viewModel.setCurrentToDoItem(lokalToDo)
                    it.findNavController().navigate(R.id.action_toDoList_to_editToDo)
                }

                checkIsDone.setOnClickListener {
                    lokalToDo.toDoLocalIsDone = checkIsDone.isChecked
                    viewModel.updateToDoItem(lokalToDo)

                    setTitleColor(lokalToDo)
                }

                checkIsFavourite.setOnClickListener {
                    lokalToDo.toDoLocalIsFavourite = checkIsFavourite.isChecked
                    viewModel.updateToDoItem(lokalToDo)

                    setFavouriteIcon()
                }
            }
        }

        private fun FragmentTodoListitemBinding.setFavouriteIcon() {
            val image: Drawable? = getDrawable()

            if (checkIsFavourite.isChecked == true) {
                tvTitle.setCompoundDrawables(
                    image,
                    null,
                    null,
                    null
                )
            } else {
                tvTitle.setCompoundDrawables(
                    null,
                    null,
                    null,
                    null
                )
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private fun getDrawable(): Drawable? {
            // WICHTIG ! Sehr lange für gesucht !!!
            // https://www.programmersought.com/article/67787700192/
            // Einzig mir bekannter Weg dafür...
            //val image: Drawable = this@ToDoViewHolder.itemView.context.resources.getDrawable(R.drawable.ic_favourite)

            val image: Drawable? =
                this@ToDoViewHolder.itemView.context.getDrawable(R.drawable.ic_favourite)

            val h = image?.intrinsicHeight
            val w = image?.intrinsicWidth

            if (image != null) {
                if (w != null) {
                    if (h != null) {
                        image.setBounds(0, 0, w, h)
                    }
                }
            }

            return image
        }

        private fun FragmentTodoListitemBinding.setTitleColor(lokalToDo: LocalToDo) {
            if (lokalToDo.toDoLocalDoUntil.isBefore(LocalDateTime.now())) {
                if (lokalToDo.toDoLocalDoUntil.isBefore(LocalDateTime.now())) {
                    if (checkIsDone.isChecked == false) {
                        tvTitle.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.isLate
                            )
                        )
                    } else {
                        tvTitle.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.isDone
                            )
                        )
                    }
                } else if (lokalToDo.toDoLocalDoUntil.isAfter(LocalDateTime.now())) {
                    if (checkIsDone.isChecked == false) {
                        tvTitle.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.black_overlay
                            )
                        )
                    } else {
                        tvTitle.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.isDone
                            )
                        )
                    }
                }
            }
        }
    }

    class ToDoComparator : DiffUtil.ItemCallback<LocalToDo>() {
        override fun areItemsTheSame(oldItemLokal: LocalToDo, newItemLokal: LocalToDo): Boolean {
            return oldItemLokal === newItemLokal
        }

        override fun areContentsTheSame(oldItemLokal: LocalToDo, newItemLokal: LocalToDo): Boolean {
            return oldItemLokal.toDoLocalId == newItemLokal.toDoLocalId
        }
    }
}