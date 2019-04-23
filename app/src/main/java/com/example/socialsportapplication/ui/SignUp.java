

package com.example.socialsportapplication.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialsportapplication.Firebase;
import com.example.socialsportapplication.Models.UserModel;
import com.example.socialsportapplication.R;
import com.example.socialsportapplication.Util.CommonUtils;
import com.example.socialsportapplication.Util.Constants;
import com.example.socialsportapplication.Util.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//References used:
//        https://www.youtube.com/watch?v=0NFwF7L-YA8&t=175s

public class SignUp extends AppCompatActivity {


    private static final String TAG = "SignUp";

    private static final String KEY_USER = "user_email";
    private static final String KEY_PASSWORD = "user_pass";

    private EditText email, password;
    private Button register;
    private TextView login;
    private ProgressDialog progressDialog;
    //private ISignUp mISignUp;
    private SharedPref sharedPref;

    private FirebaseFirestore db = Firebase.getInstance().db();
    private FirebaseAuth mAuth = Firebase.getInstance().auth();
    List<String> matchesList = new ArrayList<>();
    List<String> joinList = new ArrayList<>();
    List<String> friendList = new ArrayList<>();
    private HashMap<String, String> requestedusers=new HashMap<>();
    DatabaseReference mFirebaseDatabase;
    CommonUtils commonUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String b = "hello";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        commonUtils = new CommonUtils(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        register = findViewById(R.id.buttonRegister);
        login = findViewById(R.id.textViewSignIn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        sharedPref = new SharedPref(SignUp.this);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (commonUtils.isNetworkAvailable()){
                    registerUser();
                }else {
                    Toast.makeText(SignUp.this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void registerUser() {

        String email1 = email.getText().toString().trim();
        String password1 = password.getText().toString().trim();

        if (TextUtils.isEmpty(email1)) {
            //no email entered
            //Toast.makeText(this, "No email address entered. Please enter email address.", Toast.LENGTH_SHORT).show();
            email.setError("No email address entered. Please enter email address.");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email1).matches()) {
            email.setError("Please enter a valid email.");
            email.requestFocus();
            return;
        }

        if (password1.length() < 6) {
            password.setError("Password must have at least 6 characters.");
            password.requestFocus();
            return;
        }


        if (TextUtils.isEmpty(password1)) {
            //no password entered
            //Toast.makeText(this, "No password entered. Please enter password.", Toast.LENGTH_SHORT).show();
            password.setError("No password entered. Please enter password.");
            password.requestFocus();
            return;
        }

        progressDialog.setMessage("Registering Account...");
        progressDialog.show();
        //progressDialog.dismiss();


        mAuth.createUserWithEmailAndPassword(email1, password1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            //user creates account
                            Toast.makeText(SignUp.this, "Account created.", Toast.LENGTH_SHORT).show();
                            //add data to firebase
                            addRegisteredDataToFirebase();


                        } else {
                            if (progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                Toast.makeText(getApplicationContext(), "This email address is already registered.", Toast.LENGTH_LONG).show();

                            } else {

                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                Toast.makeText(SignUp.this, "Failed to create account.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }


    private void addRegisteredDataToFirebase() {
        matchesList.add("member");
        UserModel usermodel = new UserModel(email.getText().toString().trim(),friendList, matchesList,joinList);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        mFirebaseDatabase.child("users").child(userId).setValue(usermodel, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    progressDialog.dismiss();
                    Log.e(TAG, "Data could not be saved " + databaseError.getMessage());
                } else {
                    Intent intent = new Intent(SignUp.this, HomeActivity.class);
                    Log.d("FirebaseTest", "2 worked");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    sharedPref.setBoolean(Constants.isSocialSportLogin, true);
                    startActivity(intent);
                    finish();
                }
            }


        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}


