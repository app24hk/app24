package com.capstone.app24.bean;

/**
 * Created by amritpal on 4/11/15.
 */
public class LatestFeedsModel {
    private String feedHeading;
    private String feedBody;
    private String creatorName;
    private String createdTime;
    private String profileCountLoginUser;
    private String seen;

    public String getFeedHeading() {
        return feedHeading;
    }

    public void setFeedHeading(String feedHeading) {
        this.feedHeading = feedHeading;
    }

    public String getFeedBody() {
        return feedBody;
    }

    public void setFeedBody(String feedBody) {
        this.feedBody = feedBody;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getProfileCountLoginUser() {
        return profileCountLoginUser;
    }

    public void setProfileCountLoginUser(String profileCountLoginUser) {
        this.profileCountLoginUser = profileCountLoginUser;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }
}
