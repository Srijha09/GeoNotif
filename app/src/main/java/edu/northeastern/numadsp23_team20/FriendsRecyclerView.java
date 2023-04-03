package edu.northeastern.numadsp23_team20;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.List;

import edu.northeastern.numadsp23_team20.R;

public class FriendsRecyclerView extends RecyclerView.Adapter<FriendsRecyclerView.ViewHolder> {

    private List<FriendsData> mData; // replace String with your data type

    private OnButtonClickListener mListener;


    // Constructor
    public FriendsRecyclerView(List<FriendsData> data, OnButtonClickListener listener) {
        mData = data;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FriendsRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_friends, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mListener);
        return viewHolder;
    }




    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get element from your dataset at this position
        // replace String with your data type
        FriendsData data = mData.get(position);
        // set the data to the view holder's views
        holder.userName.setText(data.getUserName());
        holder.button.setText(data.getButtonDetails());


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnButtonClickListener {
        void onButtonClickChange(int position);
    }

    // Provide a reference to the views for each data item
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;

        public Button button;

        public ViewHolder(View view, OnButtonClickListener listener) {
            super(view);
            userName = view.findViewById(R.id.name_of_person);
            button = view.findViewById(R.id.follow_button);

            button.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onButtonClickChange(position);
                    notifyItemChanged(position); // Notify the adapter that the data has changed
                }
            });
        }



    }

}