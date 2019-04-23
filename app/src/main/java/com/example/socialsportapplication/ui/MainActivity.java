package com.example.socialsportapplication.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.socialsportapplication.R;
import com.example.socialsportapplication.Util.CommonUtils;
import com.example.socialsportapplication.Util.Constants;
import com.example.socialsportapplication.Util.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.IgnoreExtraProperties;

import io.fabric.sdk.android.Fabric;

//Refernces used:
//https://www.youtube.com/watch?v=tJVBXCNtUuk

@IgnoreExtraProperties
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";



    private TextView signup, forgotPassword;
    private Button login;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private EditText email, password;
    private ProgressDialog progressDialog;
    private SharedPref sharedPref;
    CommonUtils commonUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        FirebaseApp.initializeApp(this);
        commonUtils = new CommonUtils(this);
        sharedPref = new SharedPref(MainActivity.this);
        if (sharedPref.getBoolean(Constants.isSocialSportLogin)) {
            loginSucess();
        }

        setContentView(R.layout.activity_main);

        signup = findViewById(R.id.textViewSignUp);
        findViewById(R.id.buttonLogin).setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        forgotPassword = findViewById(R.id.textForgotPass);

        mAuth = FirebaseAuth.getInstance();




        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                    startActivity(intent);
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    Intent intent = new Intent(getApplicationContext(), SignUp.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


    private void userLogin() {


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

        // progressDialog.setMessage("Login successful.");

            progressDialog.show();

        mAuth.signInWithEmailAndPassword(email1, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Log.d("Login test", "1 worked");
                    Toast.makeText(MainActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    loginSucess();
                } else {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void loginSucess() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        Log.d("sajid", "2 worked");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        sharedPref.setBoolean(Constants.isSocialSportLogin, true);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonLogin:

                if (commonUtils.isNetworkAvailable()){
                    userLogin();
                }else {
                    Toast.makeText(this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }


}


