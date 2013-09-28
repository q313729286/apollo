/*
 * Copyright (C) 2013 Seker. All rights reserved.
 */
package seker.collabfilter;

/**
 * 
 * @author seker
 * @since 2013-9-28
 */
public class RatingInfo {
    
    private String mUserID;
    private String mMovieID;
    private float mRating;
    private long mTimeStamp;
    
    public RatingInfo(String userID, String movieID, float rating, long time) {
        mUserID = userID;
        mMovieID = movieID;
        mRating = rating;
        mTimeStamp = time;
    }
    
    /**
     * @return the mMovieID
     */
    public String getMovieID() {
        return mMovieID;
    }
    /**
     * @param movieID the mMovieID to set
     */
    public void setMovieID(String movieID) {
        mMovieID = movieID;
    }
    /**
     * @return the mUserID
     */
    public String getUserID() {
        return mUserID;
    }
    /**
     * @param userID the mUserID to set
     */
    public void setUserID(String userID) {
        mUserID = userID;
    }
    /**
     * @return the mRating
     */
    public float getRating() {
        return mRating;
    }
    /**
     * @param rating the mRating to set
     */
    public void setRating(float rating) {
        mRating = rating;
    }
    /**
     * @return the mTimeStamp
     */
    public long getTimeStamp() {
        return mTimeStamp;
    }
    /**
     * @param timeStamp the mTimeStamp to set
     */
    public void setTimeStamp(long timeStamp) {
        mTimeStamp = timeStamp;
    }
    
    @Override
    public String toString() {
        return "RatingInfo [mUserID=" + mUserID + ", mMovieID=" + mMovieID + ", mRating=" + mRating + ", mTimeStamp="
                + mTimeStamp + "]";
    }
}
