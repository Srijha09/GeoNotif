package edu.northeastern.numadsp23_team20;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.ViewHolder> {
    private final ArrayList<User> memberList;
    private ItemClickListener listener;
    private final Context context;
    //String currentGroup;

    //Creating the constructor
    public AddMemberAdapter(ArrayList<User> memberList, Context context) {
        this.memberList = memberList;
        this.context = context;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.member_listview_item, parent,
                false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = memberList.get(position);
        holder.username.setText(user.getUsername());
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ViewHolder(@NonNull View itemView, final ItemClickListener listener) {
            super(itemView);

            username = itemView.findViewById(R.id.name_of_person);
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

