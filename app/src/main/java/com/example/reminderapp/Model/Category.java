package com.example.reminderapp.Model;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String imagename;
    private String name;

    public Category(){


    }

    public Category(int id, String imagename, String name) {
        this.id = id;
        this.imagename = imagename;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
