package com.example.socialsportapplication.Models;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserModel {
    private String email;
    private List<String> member = new ArrayList<>();
    private List<String> join = new ArrayList<>();
    private List<String> friends = new ArrayList<>();


    public UserModel() {
    }

    public UserModel(String email,List<String> friendid, List<String> member,List<String> join) {
        this.email = email;
        this.friends=friendid;
        this.member = member;
        this.join = join;
    }

    public List<String> getJoin() {
        return join;
    }

    public void setJoin(List<String> join) {
        this.join = join;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getMember() {
        return member;
    }

    public void setMember(List<String> member) {
        this.member = member;
    }


    public List<String> getFriendid() {
        return friends;
    }

    public void setFriendid(List<String> friendid) {
        this.friends = friendid;
    }


    @Override
    public boolean equals(Object obj) {
        if (this.member.equals(((UserModel) obj).member)) {
            return true;
        }

        if (this.join.equals(((UserModel) obj).join)) {
            return true;
        }

        if (this.friends.equals(((UserModel) obj).friends)) {
            return true;
        }
        return false;
    }
}
