package edu.northeastern.numadsp23_team20;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieLoadingHolder extends RecyclerView.ViewHolder {

    private ProgressBar spinner;

    public MovieLoadingHolder(@NonNull View itemView) {
        super(itemView);
        this.spinner = itemView.findViewById(R.id.MovieLoadingSpinner);
    }
}
