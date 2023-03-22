package edu.northeastern.numadsp23_team20;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TasksFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_tasks, container, false);
        inflatedView.findViewById(R.id.TempTvuiTrigger).setOnClickListener(l -> {
            Intent intent = new Intent(getContext(), TaskView.class);
            this.startActivity(intent);
        });
        return inflatedView;
    }

}