package com.capstone.app24.bean;

import android.graphics.Bitmap;

/**
 * Created by amritpal on 8/12/15.
 */
public class GalleryModel {
    Bitmap image;
    String path;
    int id;

    public GalleryModel() {
    }

    public GalleryModel(int id, String path, Bitmap image) {
        this.id = id;
        this.path = path;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
