package edu.northeastern.numadsp23_team20;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StickerFragment extends Fragment {
    private int countCharmander;
    private int countGroudon;
    private int countJolteon;
    private int countMewTwo;
    private int countPikachu;
    private int countSquirtle;
    private FirebaseDatabase mDatabase;

    public StickerFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sticker, container, false);
    }
}