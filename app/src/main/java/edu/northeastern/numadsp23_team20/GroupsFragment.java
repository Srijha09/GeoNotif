package edu.northeastern.numadsp23_team20;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        //openGroupTask();
        return view;
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