package com.plfdev.to_do_list.tasks.presenter.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.plfdev.to_do_list.databinding.DialogTaskBinding
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
                if(tasks.isEmpty()) {
                    binding.rvTasks.visibility = View.GONE
                    binding.emptyList.visibility = View.VISIBLE
                } else {
                    binding.rvTasks.visibility = View.VISIBLE
                    binding.emptyList.visibility = View.GONE
                    adapter.submitList(tasks)
                }
            }
        }

        binding.addButton.setOnClickListener {
            showDialog(
                context = requireContext(),
                onSave = { task ->
                    val newTask = task.copy(
                        isAdded = true
                    )
                    viewModel.addTask(newTask)
                }
            )
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
                showDialog(
                    task = task,
                    context = requireContext(),
                    onSave = { taskItem ->
                        val taskUpdated = taskItem.copy(
                            isUpdated = true,
                            isSynced = false
                        )
                        viewModel.updateTask(taskUpdated)
                    }
                )
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

    private fun showDialog(
        task: Task? = null,
        context: Context,
        onSave: (Task) -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        val dialogBinding = DialogTaskBinding.inflate(layoutInflater)
        var dialogTitle: String = ""

        if(task != null) {
            dialogTitle = "Edit Task"
            dialogBinding.editTextTitle.setText(task.title)
            dialogBinding.editTextDescription.setText(task.description)
            dialogBinding.checkBoxIsCompleted.isChecked = task.isCompleted
        } else {
            dialogTitle =  "Add Task"
        }

        builder.setView(dialogBinding.root)
            .setTitle(dialogTitle)
            .setPositiveButton("Save") { _, _ ->
                val newTitle = dialogBinding.editTextTitle.text.toString()
                val newDescription = dialogBinding.editTextDescription.text.toString()
                val newIsCompleted = dialogBinding.checkBoxIsCompleted.isChecked

                if(newTitle.isEmpty()) {
                    dialogBinding.editTextTitle.error = "This field cant be empty"
                } else {
                    val newTask = Task(
                        id = task?.id,
                        title = newTitle,
                        description = newDescription,
                        isCompleted = newIsCompleted,
                        isAdded = task?.isAdded ?: false,
                    )

                    onSave.invoke(newTask)
                }

            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}