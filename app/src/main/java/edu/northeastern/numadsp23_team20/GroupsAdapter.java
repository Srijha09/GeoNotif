package edu.northeastern.numadsp23_team20;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
    private final ArrayList<Groups> groupsList;
    private ItemClickListener listener;
    private final Context context;
    //String currentGroup;

    //Creating the constructor
    public GroupsAdapter(ArrayList<Groups> groupsList,Context context) {
        this.groupsList = groupsList;
        this.context = context;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.groups_item, parent, false);
        return new GroupsAdapter.ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Groups group = groupsList.get(position);
        holder.groupname.setText(group.getUsername());
        if(group.getImageURL().equals("default")){
            holder.groupImage.setImageResource(R.drawable.groups);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TasksFragment tasksFragment = new TasksFragment();
                FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.FrameLayout, tasksFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView groupname;
        public ImageView groupImage;
        public ViewHolder(@NonNull View itemView, final ItemClickListener listener) {
            super(itemView);

            groupname = itemView.findViewById(R.id.groupname);
            groupImage = itemView.findViewById(R.id.groupimage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getLayoutPosition();
                        if (position != RecyclerView.NO_POSITION) {

                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}
