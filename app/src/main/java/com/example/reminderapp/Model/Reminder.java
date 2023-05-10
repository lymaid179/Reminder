package com.example.reminderapp.Model;

import com.example.reminderapp.Interface.ItemClicked;
import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Reminder implements Serializable  {

    private int id;

    private Category image;
    private String title, content;

    private Timestamp time;
    private boolean checkDone = false;

    private String userEmail;
    public Reminder(){

    }


    public Reminder( int id,Category image, String title,String content, Timestamp time, boolean checkDone, String userEmail) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.content = content;
        this.time = time;
        this.checkDone = checkDone;
        this.userEmail = userEmail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Category getImage() {
        return image;
    }

    public void setImage(Category image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public boolean isCheckDone() {
        return checkDone;
    }

    public void setCheckDone(boolean checkDone) {
        this.checkDone = checkDone;
    }
}
