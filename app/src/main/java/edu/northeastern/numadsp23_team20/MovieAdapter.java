package edu.northeastern.numadsp23_team20;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Movie> movieList;
    private Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public MovieAdapter(List<Movie> movieList, Context context) {
        this.movieList = movieList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, null);
//            return new MovieViewHolder(view);
            return new MovieViewHolder(LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false));
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_loading, null);
            return new MovieLoadingHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            populateItemRows((MovieViewHolder) holder, position);
        } else if (holder instanceof MovieLoadingHolder) {
            showLoadingView((MovieLoadingHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return this.movieList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.movieList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private void showLoadingView(MovieLoadingHolder viewHolder, int position) {
        //ProgressBar would be displayed
    }

    private void populateItemRows(MovieViewHolder viewHolder, int position) {
        viewHolder.bindData(this.movieList.get(position));
    }
}
