package edu.northeastern.numadsp23_team20;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskTypeListAdapter extends RecyclerView.Adapter<TaskTypeListAdapter.ViewHolder> {

    List<String> arrayList;
    OnTaskTypeAssigneeItemClickListener onTaskTypeAssigneeItemClickListener;
    int selectedPosition = -1;

    public TaskTypeListAdapter(List<String> arrayList, OnTaskTypeAssigneeItemClickListener onTaskTypeAssigneeItemClickListener)
    {
        this.arrayList = arrayList;
        this.onTaskTypeAssigneeItemClickListener = onTaskTypeAssigneeItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tasktype_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.assigneeRadioButton.setText(arrayList.get(position));
        holder.assigneeRadioButton.setChecked(position == selectedPosition);
        holder.assigneeRadioButton.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                {
                    if (b) {
                        selectedPosition = holder.getAdapterPosition();
                        onTaskTypeAssigneeItemClickListener.onClick(holder.assigneeRadioButton.getText().toString());
                    }
                }
            });
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public int getItemViewType(int position) {
        return position;
    }

    @Override public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton assigneeRadioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.assigneeRadioButton = itemView.findViewById(R.id.AssigneeRadioButton);
        }
    }
}
