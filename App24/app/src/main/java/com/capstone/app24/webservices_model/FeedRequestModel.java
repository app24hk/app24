package com.capstone.app24.webservices_model;

/**
 * Created by amritpal on 27/11/15.
 */
public class FeedRequestModel {
    String user_id;
    String title;
    String description;
    String media;
    String type;
    boolean result;
    String message;
    String base64String;
    String mediaId;
    String thumbnail;

    public FeedRequestModel() {
    }

    public FeedRequestModel(String user_id, String title, String description, String media, String type,
                            boolean result, String message, String thumbnail) {
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.media = media;
        this.type = type;
        this.result = result;
        this.message = message;
        this.thumbnail = thumbnail;
    }

    public FeedRequestModel(String user_id, String title, String description, String media, String type,
                            String base64String, String mediaId, boolean result, String message, String thumbnail) {
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.media = media;
        this.type = type;
        this.base64String = base64String;
        this.result = result;
        this.message = message;
        this.mediaId = mediaId;
        this.thumbnail = thumbnail;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBase64String() {
        return base64String;
    }

    public void setBase64String(String base64String) {
        this.base64String = base64String;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
