package com.capstone.app24.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by amritpal on 4/11/15.
 */
public class UserFeedModel implements Parcelable {
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

    public UserFeedModel() {
    }

    public UserFeedModel(String id, String title, String description, String media, String
            type, String user_id, String created, String modified, String user_name, String
                                 viewcount, String thumbnail) {
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

    public static final Parcelable.Creator<UserFeedModel> CREATOR = new Creator<UserFeedModel>() {
        @Override
        public UserFeedModel createFromParcel(Parcel source) {
            return new UserFeedModel(source);
        }

        @Override
        public UserFeedModel[] newArray(int size) {
            return new UserFeedModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(media);
        dest.writeString(type);
        dest.writeString(user_id);
        dest.writeString(created);
        dest.writeString(modified);
        dest.writeString(user_name);
        dest.writeString(viewcount);
        dest.writeString(thumbnail);
    }

    public UserFeedModel(Parcel source) {
        this.id = source.readString();
        this.title = source.readString();
        this.description = source.readString();
        this.media = source.readString();
        this.type = source.readString();
        this.user_id = source.readString();
        this.created = source.readString();
        this.modified = source.readString();
        this.user_name = source.readString();
        this.viewcount = source.readString();
        this.thumbnail = source.readString();
    }

}
