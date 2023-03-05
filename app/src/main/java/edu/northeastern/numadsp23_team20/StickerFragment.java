package edu.northeastern.numadsp23_team20;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StickerFragment extends Fragment {
    private TextView countCharmander;
    private TextView countGroudon;
    private TextView countJolteon;
    private TextView countMewTwo;
    private TextView countPikachu;
    private TextView countSquirtle;
    private FirebaseDatabase mDatabase;
    private DatabaseReference reference;

    String username;

    public StickerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sticker, container, false);
        countCharmander = view.findViewById(R.id.countCharmander);
        countGroudon = view.findViewById(R.id.countGroudon);
        countMewTwo = view.findViewById(R.id.countMewtwo);
        countPikachu = view.findViewById(R.id.countPikachu);
        countJolteon = view.findViewById(R.id.countJolteon);
        countSquirtle = view.findViewById(R.id.countSquirtle);
        ChooseChat activity = (ChooseChat) getActivity();
        assert activity != null;
        Bundle bundle = activity.getMyData();
        username = bundle.getString("current_user");
        System.out.println(username);
        getCount();
        return view;
    }

    public void getCount() {
        mDatabase = FirebaseDatabase.getInstance();
        reference = mDatabase.getReference().child("Users/" + username + "/stickerCount");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
                HashMap<String, Long> stickerCount = (HashMap<String, Long>) snapshot.getValue();
                assert stickerCount != null;
                countCharmander.setText("0");
                countGroudon.setText("0");
                countMewTwo.setText("0");
                countPikachu.setText("0");
                countJolteon.setText("0");
                countSquirtle.setText("0");
                for (String key : stickerCount.keySet()) {
                    if (key.equals("charmander")) {
                        countCharmander.setText(Objects.requireNonNull(stickerCount.get(key)).toString());
                    }
                    if (key.equals("groudon")) {
                        countGroudon.setText(Objects.requireNonNull(stickerCount.get(key)).toString());
                    }
                    if (key.equals("mewtwo")) {
                        countMewTwo.setText(Objects.requireNonNull(stickerCount.get(key)).toString());
                    }
                    if (key.equals("pikachu")) {
                        countPikachu.setText(Objects.requireNonNull(stickerCount.get(key)).toString());
                    }
                    if (key.equals("jolteon")) {
                        countJolteon.setText(Objects.requireNonNull(stickerCount.get(key)).toString());
                    }
                    if (key.equals("squirtle")) {
                        countSquirtle.setText(Objects.requireNonNull(stickerCount.get(key)).toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}