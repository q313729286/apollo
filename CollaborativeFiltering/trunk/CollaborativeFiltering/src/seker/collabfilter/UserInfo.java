/*
 * Copyright (C) 2013 Seker. All rights reserved.
 */
package seker.collabfilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author seker
 * @since 2013-9-28
 */
public class UserInfo {
    private String mUserID;
    private List<RatingInfo> mInfos = new ArrayList<RatingInfo>();
    
    public UserInfo(String userID) {
        mUserID = userID;
    }
    
    public void addRatingInfo(RatingInfo info) {
        mInfos.add(info);
    }
    
    /**
     * @return the userID
     */
    public String getUserID() {
        return mUserID;
    }
    /**
     * @param userID the userID to set
     */
    public void setUserID(String userID) {
        mUserID = userID;
    }
    /**
     * @return the mInfos
     */
    public List<RatingInfo> getInfos() {
        return mInfos;
    }
    /**
     * @param mInfos the mInfos to set
     */
    public void setInfos(List<RatingInfo> infos) {
        mInfos = infos;
    }
}
