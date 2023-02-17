package edu.northeastern.numadsp23_team20;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;

public class MovieViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView releaseYear;
    public ImageView moviePoster;
    public String imageUrl;

    public MovieViewHolder(@NonNull View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.MovieTitleView);
        releaseYear = (TextView) itemView.findViewById(R.id.MovieReleaseYearView);
        moviePoster = (ImageView) itemView.findViewById(R.id.MoviePoster);
    }

    public void bindData(Movie movieToBind) {
        this.title.setText(movieToBind.getTitle());
        String releaseYearText = "Release year: " + movieToBind.getReleaseYear();
        this.releaseYear.setText(releaseYearText);
        if (movieToBind.getMovieImageUrl() != "") {
            imageUrl = movieToBind.getMovieImageUrl();
            DownloadImageFromInternet getImage = new DownloadImageFromInternet();
            Thread runnableThread = new Thread(getImage);
            runnableThread.start();
        }
    }

    private class DownloadImageFromInternet implements Runnable {
        @Override
        public void run() {
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageUrl).openStream();
                bimage = BitmapFactory.decodeStream(in);
                if (bimage != null)
                    moviePoster.setImageBitmap(bimage);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
