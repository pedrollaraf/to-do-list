import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.plfdev.to_do_list.databinding.TodolistItemBinding
import com.plfdev.to_do_list.tasks.domain.model.ToDoListUiModel

class ToDoListAdapter(
    private val onDeleteTask: (ToDoListUiModel) -> Unit,
    private val onEditTask: (ToDoListUiModel) -> Unit,
) : ListAdapter<ToDoListUiModel, ToDoListAdapter.ToDoListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoListViewHolder {
        val binding = TodolistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoListViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class ToDoListViewHolder(private val binding: TodolistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: ToDoListUiModel) {
            binding.taskName.text = task.title
            binding.taskDescription.text = task.description
            binding.editTask.setOnClickListener { onEditTask(task) }
            binding.deleteTask.setOnClickListener { onDeleteTask(task) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ToDoListUiModel>() {
            override fun areItemsTheSame(
                oldItem: ToDoListUiModel,
                newItem: ToDoListUiModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ToDoListUiModel,
                newItem: ToDoListUiModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}
