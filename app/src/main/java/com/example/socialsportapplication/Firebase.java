package com.example.socialsportapplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Firebase {
    private static Firebase instance;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private Firebase(){
    }

    public static Firebase getInstance(){
        if(instance == null) {
            instance = new Firebase();
        }
        return instance;
    }

    public FirebaseAuth auth(){
        return mAuth;
    }

    public void setAuth(FirebaseAuth auth){
        this.mAuth = auth;
    }

    public FirebaseFirestore db(){
        return db;
    }

    public void setDB(FirebaseFirestore db){
        this.db = db;
    }




}
