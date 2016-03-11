package app24.feedbook.hk.bean;

import android.graphics.Bitmap;

/**
 * Created by amritpal on 8/12/15.
 */
public class GalleryModel {
    Bitmap image;
    String path;
    int id;
    boolean isVideo;

    public GalleryModel() {
    }

    public GalleryModel(int id, String path, Bitmap image, boolean isVideo) {
        this.id = id;
        this.path = path;
        this.image = image;
        this.isVideo = isVideo;
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

    public boolean isVideo() {
        return isVideo;
    }

    public void setIsVideo(boolean isVideo) {
        this.isVideo = isVideo;
    }
}
