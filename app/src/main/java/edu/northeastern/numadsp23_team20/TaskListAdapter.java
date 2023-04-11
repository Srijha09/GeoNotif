package edu.northeastern.numadsp23_team20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder>
        implements OnTaskItemClickListener {

    private List<Task> taskList;
    private OnTaskItemClickListener onTaskItemClickListener;

    public TaskListAdapter(List<Task> taskList, OnTaskItemClickListener onTaskItemClickListener) {
        this.taskList = taskList;
        this.onTaskItemClickListener = onTaskItemClickListener;
    }

    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View taskRecyclerView = inflater.inflate(R.layout.task_recyclerview_item, parent, false);
        return new TaskListViewHolder(taskRecyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
        Task task = this.taskList.get(position);
        holder.RVTaskTitle.setText(task.getTaskName());
        holder.RVTaskLocation.setText("\uD83D\uDCCD " + task.getLocation().getKey());
    }

    @Override
    public int getItemCount() {
        return this.taskList.size();
    }

    @Override
    public void onTaskItemClick(int position) {}

    public class TaskListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView RVTaskTitle;
        private TextView RVTaskLocation;

        public TaskListViewHolder(@NonNull View itemView) {
            super(itemView);

            this.RVTaskTitle = itemView.findViewById(R.id.RVTaskTitle);
            this.RVTaskLocation = itemView.findViewById(R.id.RVTaskLocation);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onTaskItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onTaskItemClickListener.onTaskItemClick(position);
                }
            }
        }
    }
}
