package com.plfdev.to_do_list.tasks.presenter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.plfdev.to_do_list.databinding.FragmentTaskBinding
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.presenter.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskFragment : Fragment() {

    private lateinit var binding: FragmentTaskBinding
    private val viewModel: TaskViewModel by viewModel()
    private lateinit var adapter: TaskAdapter

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
        setupAdapter()

        lifecycleScope.launch {
            viewModel.tasks.collect { tasks ->
                adapter.submitList(tasks)
            }
        }

        binding.addButton.setOnClickListener {
            val newToDoItem = Task(
                title = "New Item",
                description = "New description item",
                isAdded = true
            )
            viewModel.addTask(newToDoItem)
        }

        binding.syncButton.setOnClickListener {
            viewModel.syncTasks()
        }
    }

    private fun setupAdapter() {
        adapter = TaskAdapter(
            onDeleteTask = { task ->
                val deletedTask = task.copy(
                    isDeleted = true,
                    isSynced = false
                )
                viewModel.updateTask(deletedTask)
            },
            onEditTask = { task ->
                val updatedTask = task.copy(
                    title = "TOTTENHAM",
                    isUpdated = true,
                    isSynced = false
                )
                viewModel.updateTask(updatedTask)
            },
            onUndoTask = { task ->
                val undoTask = task.copy(
                    isDeleted = false,
                    isSynced = false,
                    isUpdated = true
                )
                viewModel.updateTask(undoTask)
            }
        )

        binding.rvTasks.adapter = adapter
    }
}