/*
 * Copyright (C) 2013 Baidu Inc. All rights reserved.
 */
package seker.collabfilter;

/**
 * 
 * @author seker
 * @since 2013-9-28
 */
public class MovieInfo {
    
    private String mGenres;
    private String mMovieID;
    private String mTitle;
    
    public MovieInfo(String movieID, String title, String genres) {
        mMovieID = movieID;
        mTitle = title;
        mGenres = genres;
    }
    
    public String getGenres() {
        return mGenres;
    }
    public String getMovieID() {
        return mMovieID;
    }
    public String getTitle() {
        return mTitle;
    }
    public void setGenres(String genres) {
        mGenres = genres;
    }
    public void setMovieID(String movieID) {
        mMovieID = movieID;
    }
    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public String toString() {
        return "MovieInfo [mMovieID=" + mMovieID + ", mTitle=" + mTitle + ", mGenres=" + mGenres + "]";
    }
}
