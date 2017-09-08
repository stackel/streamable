package com.erikstackelberg.streamable.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.erikstackelberg.streamable.R;
import com.erikstackelberg.streamable.databinding.MovieGridViewBinding;
import com.erikstackelberg.streamable.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieListAdapter extends ArrayAdapter<Movie>{

    public MovieListAdapter(@NonNull Context context, @NonNull ArrayList<Movie> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null)  {
            convertView = LayoutInflater.from(
                    getContext()).inflate(R.layout.movie_grid_view, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.titleView = convertView.findViewById(R.id.movie_grid_title);
            viewHolder.posterView = convertView.findViewById(R.id.movie_grid_poster);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Movie movie = getItem(position);

        if(movie != null) {
            viewHolder.titleView.setText(movie.getTitle());
            Picasso.with(getContext())
                    .load(movie.getFullPosterUrl())
                    .into(viewHolder.posterView);
        }


        return convertView;
    }

    private static class ViewHolder {
        TextView titleView;
        ImageView posterView;
    }
}
