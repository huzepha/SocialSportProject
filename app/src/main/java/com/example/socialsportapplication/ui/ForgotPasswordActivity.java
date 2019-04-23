package com.example.socialsportapplication.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.socialsportapplication.Firebase;
import com.example.socialsportapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText email;
    private Button btnResetPassword;
    private FirebaseAuth mAuth = Firebase.getInstance().auth();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        email = findViewById(R.id.editTextEmail);
        btnResetPassword = findViewById(R.id.buttonResetPassword);
        mAuth = FirebaseAuth.getInstance();
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

    }

    private void resetPassword() {
        String email1 = email.getText().toString().trim();
        if (TextUtils.isEmpty(email1)) {
            //no email entered
            //Toast.makeText(this, "No email address entered. Please enter email address.", Toast.LENGTH_SHORT).show();
            email.setError("No email address entered. Please enter email address.");
            email.requestFocus();
            return;
        }

        progressDialog.show();
        mAuth.sendPasswordResetEmail(email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.hide();
                    resetPassDialog();
                } else {
                    progressDialog.hide();
                    email.setError("Please enter valid email");
                }
            }
        });

    }


    private void resetPassDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.forgot_instruction).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
                dialog.dismiss();
            }
        }).show();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
