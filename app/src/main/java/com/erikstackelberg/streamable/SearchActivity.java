package com.erikstackelberg.streamable;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.erikstackelberg.streamable.adapters.MovieListAdapter;
import com.erikstackelberg.streamable.models.Movie;
import com.erikstackelberg.streamable.models.MoviesResponse;
import com.erikstackelberg.streamable.services.TMDBService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {
    private TMDBService tmdbService;

    private ProgressBar searchProgressBar;

    private MovieListAdapter searchResultsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.themoviedb.org/3/")
                .build();

        tmdbService = retrofit.create(TMDBService.class);

        GridView searchResultsGridView = (GridView) findViewById(R.id.search_results_grid_view);
        searchResultsAdapter = new MovieListAdapter(getBaseContext(), new ArrayList<>());
        searchResultsGridView.setAdapter(searchResultsAdapter);

        searchProgressBar = (ProgressBar) findViewById(R.id.search_progress_bar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconified(false);

        Observable<String> queryChangedObservable = createQueryChangedObservable(searchView);
        queryChangedObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> {
                    hideProgressBar();
                    searchResultsAdapter.clear();
                })
                .filter(query -> query.length() > 2)
                .doOnNext(s -> showProgressBar())
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribe(this::search);

        return true;
    }
    private Observable<String> createQueryChangedObservable(SearchView searchView) {
        return Observable.create(emitter -> searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                emitter.onNext(s);
                return true;
            }
        }));
    }

    void search(String query) {
        Observable<MoviesResponse> moviesResponseObservable = tmdbService.searchMovies(query);
        moviesResponseObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(moviesResponse -> {
                    hideProgressBar();
                    searchResultsAdapter.clear();
                    searchResultsAdapter.addAll(moviesResponse.getMovies());
                });
    }

    void showProgressBar() {
        searchProgressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressBar() {
        searchProgressBar.setVisibility(View.GONE);
    }
}
