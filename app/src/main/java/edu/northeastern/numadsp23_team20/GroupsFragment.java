package edu.northeastern.numadsp23_team20;

import static android.text.TextUtils.isEmpty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class GroupsFragment extends Fragment {
    private RecyclerView recyclerView;
    private GroupsAdapter groupsAdapter;
    private Button startGroup;
    private ArrayList<Groups> groupsList;
    private String current_groups;
    private FirebaseDatabase mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        groupsList = new ArrayList<>();
        groupsList.add(new Groups("Group1", "default"));
        groupsList.add(new Groups("Group2", "default"));
//        ChooseChat activity = (ChooseChat) getActivity();
//        assert activity != null;
//        Bundle bundle = activity.getMyData();
//        current_groups = bundle.getString("current_user");
        groupsAdapter = new GroupsAdapter(groupsList, getContext());
        recyclerView.setAdapter(groupsAdapter);
        startGroup = view.findViewById(R.id.groupStart);
        startGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialogBox();
            }
        });
        //deleting a group when swiped left
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getLayoutPosition();
                // Create and show the alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("Are you sure you want to delete this group?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        groupsList.remove(position);
                        groupsAdapter.notifyItemRemoved(position);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        groupsAdapter.notifyItemChanged(position);
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        //openGroupTask();
        return view;
    }


    /**
     * alert dialogbox to add a new list view
     */
    private void addDialogBox(){
        View view=getLayoutInflater().inflate(R.layout.activity_createa_group_dialog, null);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
        alertDialog.setView(view);
        Button saveButton = view.findViewById(R.id.donebttn);
        Button cancelButton = view.findViewById(R.id.cancelbttn);
        EditText group_name = view.findViewById(R.id.editgroupname);
        ImageView image_url = view.findViewById(R.id.groupimage);
        AlertDialog alert = alertDialog.create();
        alert.show();
        saveButton.setOnClickListener(view1 -> {
            if(!isEmpty(group_name.getText().toString()) && !isEmpty(image_url.toString())){
                String groupname = group_name.getText().toString();
                String imageurl = image_url.toString();
                Groups groups = new Groups(groupname, imageurl);
                groupsList.add(groups);
                alert.dismiss();

            }else{
                Toast.makeText(requireContext(), "Fields cannot be empty",
                        Toast.LENGTH_SHORT).show();
            }

        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });

        alert.show();


    }

    private void openGroupTask() {

//        mDatabase = FirebaseDatabase.getInstance();
//        mDatabase.getReference().child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot d : dataSnapshot.getChildren()) {
//                        Groups user = new Groups(d.getKey().toUpperCase(Locale.ROOT));
//                        if (!current_groups.equals(d.getKey())) {
//                            groupsList.add(user);
//                        }
//                    }
//                }
//                groupsAdapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }


}