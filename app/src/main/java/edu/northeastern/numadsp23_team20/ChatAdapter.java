package edu.northeastern.numadsp23_team20;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private final ArrayList<Chat> chatList;
    private ItemClickListener listener;
    private final Context context;

    //Creating the constructor
    public ChatAdapter(ArrayList<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent,false);
        return new ChatAdapter.ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.username.setText(chat.getUsername());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StickItToEm.class);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ViewHolder(@NonNull View itemView, final ItemClickListener listener) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
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