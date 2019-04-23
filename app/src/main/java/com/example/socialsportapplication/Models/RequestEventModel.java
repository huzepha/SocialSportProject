package com.example.socialsportapplication.Models;

public class RequestEventModel {

    String userEmail;
    String matchName;
    String matchid;
    String userId;

    public RequestEventModel() {
    }

    public RequestEventModel(String userEmail, String matchName) {
        this.userEmail = userEmail;
        this.matchName = matchName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMatchid() {
        return matchid;
    }

    public void setMatchid(String matchid) {
        this.matchid = matchid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }
}
