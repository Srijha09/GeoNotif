package edu.northeastern.numadsp23_team20;

import static android.app.Activity.RESULT_OK;
import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GroupsFragment extends Fragment {
    private RecyclerView recyclerView;
    private GroupsAdapter groupsAdapter;
    private GroupService groupService;
    private ArrayList<Group> groupsList;
    private ArrayList<String> groupParticipants;
    private String groupname;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private final String TAG="GroupFragment";
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
        this.groupsList = new ArrayList<>();
        this.groupService = new GroupService();
        mAuth = FirebaseAuth.getInstance();
        String currentUserUid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("GeoNotif/Groups/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupsList.clear(); // clear the list before adding new data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    if (group != null && group.getGroupParticipants().contains(currentUserUid)) {
                        groupsList.add(group);
                    }
                }
                groupsAdapter.notifyDataSetChanged();// notify the adapter of the data change
                System.out.println(groupsList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
        groupsAdapter = new GroupsAdapter(this.groupsList, getContext());
        recyclerView.setAdapter(groupsAdapter);

        FloatingActionButton startGroup = view.findViewById(R.id.AddGroupButton);
        startGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialogBoxToCreateAGroup();
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
        return view;
    }


    /**
     * alert dialogbox to add a new list view
     */
    private void addDialogBoxToCreateAGroup(){
        View view=getLayoutInflater().inflate(R.layout.activity_createa_group_dialog, null);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
        alertDialog.setView(view);
        EditText group_name = view.findViewById(R.id.editgroupname);
        alertDialog.setPositiveButton("DONE", (dialogInterface, i) -> {

        });
        alertDialog.setNegativeButton("CANCEL",(dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
        ArrayList<String> groupParticipants = new ArrayList<>();
        String currentUserUUID = groupService.getFirebaseUserUID();
        groupParticipants.add(currentUserUUID);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
            if(!isEmpty(group_name.getText().toString())){
                groupname = group_name.getText().toString();
                Group groups = new Group(groupname, groupParticipants);
                UUID groupUuid = UUID.randomUUID();
                groups.setUuid(groupUuid.toString());
                groupService.createGroup(groups);
                Group group = new Group(groupname, groupParticipants.size());
                groupsList.add(group);
                groupsAdapter.notifyDataSetChanged();
                alert.dismiss();

            }else{
                Toast.makeText(requireContext(), "Fields cannot be empty",
                        Toast.LENGTH_SHORT).show();
            }

        });
        alert.show();
    }

//    public void updateGroupName(int position, String newGroupName) {
//        group.setGroupName(newGroupName);
//        groupsAdapter.notifyItemChanged(position);
//    }

}