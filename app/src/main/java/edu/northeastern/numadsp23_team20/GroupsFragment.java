package edu.northeastern.numadsp23_team20;

import static android.text.TextUtils.isEmpty;

import android.app.AlertDialog;
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
        EditText group_name = view.findViewById(R.id.editgroupname);
        ImageView image_url = view.findViewById(R.id.groupimage);
        alertDialog.setPositiveButton("Save", (dialogInterface, i) -> {

        });
        alertDialog.setNegativeButton("Cancel", (dialogInterface, i) -> {
            // do something when the negative button is clicked
            dialogInterface.dismiss();
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
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