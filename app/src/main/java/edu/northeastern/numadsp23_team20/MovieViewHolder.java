package edu.northeastern.numadsp23_team20;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import org.w3c.dom.Text;

public class MovieViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView releaseYear;
    public ImageView moviePoster;
    public String imageUrl;
    private Handler handler = new Handler();
    public ImageView genreIcon;
    public TextView genre;

    public MovieViewHolder(@NonNull View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.MovieTitleView);
        releaseYear = (TextView) itemView.findViewById(R.id.MovieReleaseYearView);
        moviePoster = (ImageView) itemView.findViewById(R.id.MoviePoster);
        genreIcon = itemView.findViewById(R.id.MovieGenreIcon);
        genre = itemView.findViewById(R.id.MovieGenreView);
    }

    public void bindData(Movie movieToBind) {
        this.title.setText(movieToBind.getTitle());
        String releaseYearText = "Release year: " + movieToBind.getReleaseYear();
        this.releaseYear.setText(releaseYearText);
        String genre = movieToBind.getGenre();
        switch (genre) {
            case "horror":
                this.genreIcon.setImageResource(R.drawable.horror);
                this.genre.setText("Genre: " + "Horror");
                break;
            case "sci-fi": this.genreIcon.setImageResource(R.drawable.sci_fi);
                this.genre.setText("Genre: " + "Sci-Fi");
                break;
            case "fantasy": this.genreIcon.setImageResource(R.drawable.fantasy);
                this.genre.setText("Genre: " + "Fantasy");
                break;
            case "comedy": this.genreIcon.setImageResource(R.drawable.comedy);
                this.genre.setText("Genre: " + "Comedy");
                break;
            case "action": this.genreIcon.setImageResource(R.drawable.action);
                this.genre.setText("Genre: " + "Action");
                break;
            case "family": this.genreIcon.setImageResource(R.drawable.family);
                this.genre.setText("Genre: " + "Family");
                break;
            case "romance": this.genreIcon.setImageResource(R.drawable.romance);
                this.genre.setText("Genre: " + "Romance");
                break;
        }
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
                if (bimage != null) {
                    Bitmap finalBimage = bimage;
                    handler.post(() -> {
                        moviePoster.setImageBitmap(finalBimage);
                    });

                }
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
