package com.example.chint.blogapp;

/**
 * Created by chint on 7/3/2017.
 */

public class BlogElements{
    private String description, image, title;

    public BlogElements(String description, String image, String title) {
        this.description = description;
        this.image = image;
        this.title = title;
    }

    public BlogElements(){}

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
