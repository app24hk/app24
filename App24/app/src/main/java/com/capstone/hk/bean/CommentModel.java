package com.capstone.hk.bean;

/**
 * Created by amritpal on 14/1/16.
 */
public class CommentModel {

    private String created_time;
    private String name;
    private String id;
    private String message;
    private String comment_id;

    public CommentModel() {
    }

    public CommentModel(String created_time, String name, String id, String message, String comment_id) {
        this.created_time = created_time;
        this.name = name;
        this.id = id;
        this.message = message;
        this.comment_id = comment_id;

    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

}
