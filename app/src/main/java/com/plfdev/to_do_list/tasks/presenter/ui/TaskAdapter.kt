package com.plfdev.to_do_list.tasks.presenter.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.plfdev.to_do_list.R
import com.plfdev.to_do_list.databinding.TaskItemBinding
import com.plfdev.to_do_list.tasks.domain.model.Task

class TaskAdapter(
    private val onDeleteTask: (Task) -> Unit,
    private val onEditTask: (Task) -> Unit,
    private val onUndoTask: (Task) -> Unit,
) : ListAdapter<Task, TaskAdapter.TasksViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class TasksViewHolder(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.taskName.text = task.title
            binding.taskDescription.text = task.description

            if(task.isSynced) {
                binding.taskName.setTextColor(Color.GREEN)
            } else {
                binding.taskName.setTextColor(Color.RED)
            }

            if(task.isCompleted) {
                binding.completedTask.setImageResource(R.drawable.ic_check)
                binding.completedTask.setColorFilter(
                    ContextCompat.getColor(binding.completedTask.context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )
            } else {
                binding.completedTask.setImageResource(R.drawable.ic_normal)
                binding.completedTask.setColorFilter(
                    ContextCompat.getColor(binding.completedTask.context, R.color.black),
                    PorterDuff.Mode.SRC_IN
                )
            }

            if(task.isDeleted) {
                binding.cardTaskItem.setCardBackgroundColor(
                    ContextCompat.getColor(binding.cardTaskItem.context, R.color.lightRed)
                )
                binding.editTask.visibility = View.GONE
                binding.deleteTask.visibility = View.GONE
                binding.undoTask.visibility = View.VISIBLE
                binding.undoTask.setOnClickListener { onUndoTask(task) }
            } else {
                binding.cardTaskItem.setCardBackgroundColor(
                    ContextCompat.getColor(binding.cardTaskItem.context, R.color.white)
                )
                binding.editTask.visibility = View.VISIBLE
                binding.deleteTask.visibility = View.VISIBLE
                binding.undoTask.visibility = View.GONE
                binding.editTask.setOnClickListener { onEditTask(task) }
                binding.deleteTask.setOnClickListener { onDeleteTask(task) }
            }

        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(
                oldItem: Task,
                newItem: Task
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Task,
                newItem: Task
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}
