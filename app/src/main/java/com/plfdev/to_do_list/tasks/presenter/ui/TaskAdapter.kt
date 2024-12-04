package com.plfdev.to_do_list.tasks.presenter.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.plfdev.to_do_list.databinding.TaskItemBinding
import com.plfdev.to_do_list.tasks.domain.model.Task

class TaskAdapter(
    private val onDeleteTask: (Task) -> Unit,
    private val onEditTask: (Task) -> Unit,
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
            binding.editTask.setOnClickListener { onEditTask(task) }
            binding.deleteTask.setOnClickListener { onDeleteTask(task) }
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
