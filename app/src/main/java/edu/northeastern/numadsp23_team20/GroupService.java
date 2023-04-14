package edu.northeastern.numadsp23_team20;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GroupService {
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    public GroupService() {
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseUser = mAuth.getCurrentUser();
    }

    public void createGroup(Group group) {
        this.ref = FirebaseDatabase.getInstance().getReference("GeoNotif/Groups/"
                + group.getGroupName());
        this.ref.setValue(group);
    }
}
