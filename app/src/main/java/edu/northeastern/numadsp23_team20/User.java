package edu.northeastern.numadsp23_team20;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String fullname, username, emailId, uid;

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }

    public List<String> tasks;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User() {
    }

    public User(String fullname, String username, String emailId, String uid) {
        this.fullname = fullname;
        this.username = username;
        this.emailId = emailId;
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "User [Fullname=" + getFullname() + ", username=" + getUsername()
                + ", email=" + getEmailId() + ", uid=" + getUid() + "]";
    }
}
