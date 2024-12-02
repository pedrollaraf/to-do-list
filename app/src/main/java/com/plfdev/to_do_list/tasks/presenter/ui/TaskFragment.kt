package com.plfdev.to_do_list.tasks.presenter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.plfdev.to_do_list.databinding.FragmentTaskBinding
import com.plfdev.to_do_list.tasks.domain.model.TaskUiModel
import java.util.UUID

class TaskFragment : Fragment() {

    private lateinit var binding: FragmentTaskBinding

    private lateinit var adapter: TaskAdapter
    private var tasks = mutableListOf<TaskUiModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        for (index in 1.. 5) {
            tasks.add(
                TaskUiModel(
                    id = UUID.randomUUID().toString(),
                    title = "Task: $index",
                    description = "Task description : $index",
                )
            )
        }
        setupAdapter()
        binding.addButton.setOnClickListener {
            val newToDoItem = TaskUiModel(
                id = UUID.randomUUID().toString(),
                title = "New Item",
                description = "New description item",
                isCompleted = true
            )
            addTask(newToDoItem)
        }
    }

    private fun setupAdapter() {
        adapter = TaskAdapter(
            onDeleteTask = { task ->
                deleteTask(task)
            },
            onEditTask = { task ->
                val updatedToDoItem = task.copy(title = "TOTTENHAM")
                updateTask(updatedToDoItem)
            },
        )

        binding.rvTasks.adapter = adapter
        adapter.submitList(tasks)
    }

    private fun deleteTask(toDoItem: TaskUiModel) {
        val position = getPositionOfFirstId(toDoItem.id)
        tasks.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    private fun updateTask(toDoItem : TaskUiModel) {
        val position = getPositionOfFirstId(toDoItem.id)
        tasks[position] = toDoItem
        adapter.notifyItemChanged(position)
    }

    private fun addTask(newToDoItem: TaskUiModel) {
        tasks.add(newToDoItem)
        adapter.notifyItemInserted(tasks.size - 1)
    }


    private fun getPositionOfFirstId(taskId: String): Int {
        return tasks.indexOfFirst { it.id == taskId }
    }
}