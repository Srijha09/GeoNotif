package edu.northeastern.numadsp23_team20;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class FriendService {
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private FriendServiceReadListener friendServiceReadListener;

    public FriendService() {
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseUser = mAuth.getCurrentUser();
    }

    public void setFriendServiceReadListener(FriendServiceReadListener friendServiceReadListener) {
        this.friendServiceReadListener = friendServiceReadListener;
    }

    public void readUserFriends() {
        String userId = this.firebaseUser.getUid();
        DatabaseReference userFriendsRef = FirebaseDatabase.getInstance().getReference(
                "GeoNotif/Users/" + userId + "/Friends");
        userFriendsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> userFriends) {
                if (!userFriends.isSuccessful()) {
                    Log.e("firebase", "Error getting data", userFriends.getException());
                } else {
                   HashMap<String, String> friendUUIDs = (HashMap<String, String>) userFriends.getResult().getValue();
                    for (String friendUUID : friendUUIDs.values()) {
                        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference(
                                "GeoNotif/Users/" + friendUUID);
                        friendRef.get().addOnCompleteListener(friend -> {
                            if (!friend.isSuccessful()) {
                                Log.e("firebase", "Error getting data", friend.getException());
                            } else {
                                User f = friend.getResult().getValue(User.class);
                                friendServiceReadListener.onFriendLoad(f);
                            }
                        });
                    }
                }
            }
        });
    }

    public interface FriendServiceReadListener {
        void onFriendLoad(User friend);
    }
}
