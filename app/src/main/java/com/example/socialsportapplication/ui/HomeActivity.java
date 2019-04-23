package com.example.socialsportapplication.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.socialsportapplication.Firebase;
import com.example.socialsportapplication.R;
import com.example.socialsportapplication.fragment.HomePageFragment;
import com.example.socialsportapplication.fragment.NotificationFragment;
import com.example.socialsportapplication.fragment.ProfileFragment;
import com.example.socialsportapplication.fragment.RequestedEventFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

//References used:
//https://www.youtube.com/watch?v=UqtsyhASW74&t=63s
//https://www.youtube.com/watch?v=ZbKKxYUOH-c
//https://stackoverflow.com/questions/20237531/how-can-i-access-getsupportfragmentmanager-in-a-fragment

public class HomeActivity extends AppCompatActivity
{
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private BottomNavigationView bottomNavigationView;
    private GoogleApiClient googleApiClient;
    private Location mylocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navViewBar);
        setupNavigationView();
    }

    private void setupNavigationView() {

        if (bottomNavigationView != null) {


            Menu menu = bottomNavigationView.getMenu();
            selectFragment(menu.getItem(0));


            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }
    }


    protected void selectFragment(MenuItem item) {

        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.ic_home:

                pushFragment(new HomePageFragment());
                break;
            case R.id.ic_friends:

                pushFragment(new RequestedEventFragment());
                break;
            case R.id.ic_notifications:

                pushFragment(new NotificationFragment());
                break;

            case R.id.ic_profile:

                pushFragment(new ProfileFragment());
                break;
        }
    }



    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.rootLayout, fragment);
                ft.commit();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.rootLayout);
        fragment.onActivityResult(requestCode, resultCode, data);
    }



}
