package com.erikstackelberg.streamable.services;

import com.erikstackelberg.streamable.models.MoviesResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDBService {
    String API_BASE_URL = "https://api.themoviedb.org/3/";
    String API_BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";

    @GET("search/movie?api_key=a9c6a013fa7c8410ff0c7959ad9bdb07")
    Observable<MoviesResponse> searchMovies(@Query("query") String query);
}
