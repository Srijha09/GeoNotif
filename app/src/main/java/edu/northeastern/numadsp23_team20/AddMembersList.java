package edu.northeastern.numadsp23_team20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AddMembersList extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private AddMemberAdapter memberAdapter;
    private ArrayList<User> memberList;
    private String currentuser;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private final String TAG="AddMembersList";

    static User dataStore;
    static FirebaseUser firebaseUser;

    static List<String> friendsUI = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members_list);

        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //filterList(newText);
                return true;
            }
        });
        recyclerView = findViewById(R.id.members_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.memberList = new ArrayList<>();
        //User user1 = new User("Rutu");
        //User user2 = new User("Rahul");
        //memberList.add(user1);
        //memberList.add(user2);

        //fetch friends user IDs.
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        DatabaseReference userFriendsRef = FirebaseDatabase.getInstance().getReference(
                "GeoNotif/Users/" + userId + "/Friends");
        userFriendsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> userFriends) {
                if (!userFriends.isSuccessful()) {
                    Log.d("firebase", "Error getting data", userFriends.getException());
                } else {
                    for (DataSnapshot childSnapshot : userFriends.getResult().getChildren()) {
                        //get the user IFD
                        String userID = childSnapshot.getValue(String.class);
                        friendsUI.add(userID);
                    }

                }
            }
        });

        //fetch details of the userIDs in friends array
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Loop through all child nodes of "users"
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String uid = childSnapshot.child("uid").getValue(String.class);

                    if (friendsUI.contains(uid)) {
                        String emailID = childSnapshot.child("emailId").getValue(String.class);
                        String fullname = childSnapshot.child("fullname").getValue(String.class);
                        String username = childSnapshot.child("username").getValue(String.class);
                        dataStore = new User(fullname, username, emailID, uid);
                        memberList.add(dataStore);
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors her
                Log.d("FirebaseError", databaseError.getMessage());

            }
        });

        memberAdapter = new AddMemberAdapter(this.memberList, getApplicationContext());
        recyclerView.setAdapter(memberAdapter);

    }


}