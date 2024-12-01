package com.plfdev.to_do_list.tasks.presenter

import ToDoListAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.plfdev.to_do_list.databinding.FragmentTodolistBinding
import com.plfdev.to_do_list.tasks.domain.model.ToDoListUiModel
import java.util.UUID

class ToDoListFragment : Fragment() {

    private lateinit var binding: FragmentTodolistBinding

    private lateinit var adapter: ToDoListAdapter
    private var toDoList = mutableListOf<ToDoListUiModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodolistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        for (index in 1.. 5) {
            toDoList.add(
                ToDoListUiModel(
                    id = UUID.randomUUID().toString(),
                    title = "To Do item: $index",
                    description = "To Do item description : $index",
                )
            )
        }
        setupAdapter()
        binding.addButton.setOnClickListener {
            val newToDoItem = ToDoListUiModel(
                id = UUID.randomUUID().toString(),
                title = "New Item",
                description = "New description item",
                isCompleted = true
            )
            addToDoList(newToDoItem)
        }
    }

    private fun setupAdapter() {
        adapter = ToDoListAdapter(
            onDeleteTask = { task ->
                deleteFromToDoList(task)
            },
            onEditTask = { task ->
                val updatedToDoItem = task.copy(title = "TOTTENHAM")
                updateTodoList(updatedToDoItem)
            },
        )

        binding.rvTodolist.adapter = adapter
        adapter.submitList(toDoList)
    }

    private fun deleteFromToDoList(toDoItem: ToDoListUiModel) {
        val position = getPositionOfFirstId(toDoItem.id)
        toDoList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    private fun updateTodoList(toDoItem : ToDoListUiModel) {
        val position = getPositionOfFirstId(toDoItem.id)
        toDoList[position] = toDoItem
        adapter.notifyItemChanged(position)
    }

    private fun addToDoList(newToDoItem: ToDoListUiModel) {
        toDoList.add(newToDoItem)
        adapter.notifyItemInserted(toDoList.size - 1)
    }


    private fun getPositionOfFirstId(taskId: String): Int {
        return toDoList.indexOfFirst { it.id == taskId }
    }
}