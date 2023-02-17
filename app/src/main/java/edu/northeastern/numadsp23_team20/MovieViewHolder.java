package edu.northeastern.numadsp23_team20;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView releaseYear;

    public MovieViewHolder(@NonNull View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.MovieTitleView);
        releaseYear = (TextView) itemView.findViewById(R.id.MovieReleaseYearView);
    }

    public void bindData(Movie movieToBind) {
        this.title.setText(movieToBind.getTitle());
        String releaseYearText = "Release year: " + movieToBind.getReleaseYear();
        this.releaseYear.setText(releaseYearText);
    }
}
