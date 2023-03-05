package edu.northeastern.numadsp23_team20;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private EditText username;
    private Button login;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        username = (EditText) view.findViewById(R.id.et_username);

        login = (Button) view.findViewById(R.id.btn_login);
        login.setOnClickListener(view1 -> {
            String getuserName = username.getText().toString();
            databaseReference.child("Users")
                    .child(getuserName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Intent intent = new Intent(getActivity(), ChooseChat.class);
                                intent.putExtra("username", getuserName);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "Invalid Username", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        });
        return view;
    }


}