package com.capstone.hk.bean;

import java.io.Serializable;

/**
 * Created by amritpal on 4/11/15.
 */
public class LatestFeedsModel implements Serializable {
    private String id;
    private String title;
    private String description;
    private String media;
    private String type;
    private String user_id;
    private String created;
    private String modified;
    private String user_name;
    private String viewcount;
    private String thumbnail;
    private String fb_feed_id;
    private String profit_amount;


    public LatestFeedsModel() {
    }

    public LatestFeedsModel(String id, String title, String description, String media, String
            type, String user_id, String created, String modified, String user_name, String
                                    viewcount, String thumbnail, String profit_amount, String fb_feed_id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.media = media;
        this.type = type;
        this.user_id = user_id;
        this.created = created;
        this.modified = modified;
        this.user_name = user_name;
        this.viewcount = viewcount;
        this.thumbnail = thumbnail;
        this.profit_amount = profit_amount;
        this.fb_feed_id = fb_feed_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getViewcount() {
        return viewcount;
    }

    public void setViewcount(String viewcount) {
        this.viewcount = viewcount;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getProfit_amount() {
        return profit_amount;
    }

    public void setProfit_amount(String profit_amount) {
        this.profit_amount = profit_amount;
    }

    public String getFb_feed_id() {
        return fb_feed_id;
    }

    public void setFb_feed_id(String fb_feed_id) {
        this.fb_feed_id = fb_feed_id;
    }
}
