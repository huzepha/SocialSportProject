package com.example.socialsportapplication.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.socialsportapplication.Firebase;
import com.example.socialsportapplication.Models.MatchModel;
import com.example.socialsportapplication.Models.UserModel;
import com.example.socialsportapplication.R;
import com.example.socialsportapplication.Util.CommonUtils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//https://stackoverflow.com/questions/45144785/android-studio-attempt-to-invoke-virtual-method-on-a-null-object-reference
//https://www.youtube.com/watch?v=EM2x33g4syY
//https://stackoverflow.com/questions/53171376/how-to-get-data-from-firebase-and-show-in-android-studio

public class CreateEventActivity extends AppCompatActivity {

    Button event;
    TextView tvMatchDate, tvMatchTime;
    EditText edtTitle, edtDescription, edtMatchLocation;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String matchLocation = "", title = "", matchDate = "", matchTime = "", description = "";
    private String lat = "", lng = "";
    private DatabaseReference mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private static final String TAG = "CreateEventActivity";
    GeoFire geoFire;
    private String timestamp;
    private ProgressDialog progressDialog;
    CommonUtils commonUtils;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference("geolocation"));
        init();
    }

    private void init() {
        event = findViewById(R.id.btn_event);
        tvMatchTime = findViewById(R.id.tv_match_time);
        tvMatchDate = findViewById(R.id.tv_match_date);
        edtTitle = findViewById(R.id.edt_match_title);
        edtDescription = findViewById(R.id.edt_description);
        edtMatchLocation = findViewById(R.id.edt_match_location);
        commonUtils = new CommonUtils(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
//        autoCompletePlace();
        tvMatchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboardFrom(v);
                openDatePicker();
            }
        });
        tvMatchTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom(v);
                openTimePicker();
            }
        });
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (commonUtils.isNetworkAvailable()){
                    title = edtTitle.getText().toString().trim();
                    matchLocation = edtMatchLocation.getText().toString().trim();
                    matchDate = tvMatchDate.getText().toString().trim();
                    matchTime = tvMatchTime.getText().toString().trim();
                    description = edtDescription.getText().toString().trim();
                    if (title.isEmpty()) {
                        Toast.makeText(CreateEventActivity.this, "Please enter match title", Toast.LENGTH_SHORT).show();
                    } else if (matchLocation.isEmpty()) {
                        Toast.makeText(CreateEventActivity.this, "Please enter match location", Toast.LENGTH_SHORT).show();
                    } else if (matchDate.isEmpty()) {
                        Toast.makeText(CreateEventActivity.this, "Please select match date", Toast.LENGTH_SHORT).show();
                    } else if (matchTime.isEmpty()) {
                        Toast.makeText(CreateEventActivity.this, "Please select match date", Toast.LENGTH_SHORT).show();
                    } else if (description.isEmpty()) {
                        Toast.makeText(CreateEventActivity.this, "Please enter match description", Toast.LENGTH_SHORT).show();
                    } else {
                        LatLng latLng = getLocationFromAddress(matchLocation);
                        if (latLng != null)
                            saveMatchToDb(latLng.latitude, latLng.longitude);
                        else
                            Toast.makeText(CreateEventActivity.this, "Location not found", Toast.LENGTH_SHORT).show();

                    }
                }else {
                    Toast.makeText(CreateEventActivity.this, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    //Save latitude & longitude in firebase
    private void saveMatchToDb(final double latitude, final double longitude) {
       // progressDialog.show();
        final String matchId = mFirebaseDatabase.push().getKey();
        Log.e(TAG, "saveMatchToDb: " + matchDate + "--->>" + matchTime);
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = null;
        try {
            date = (Date) formatter.parse(matchDate + " " + matchTime);
            Long ts = (date.getTime() / 1000);
            timestamp = ts.toString();
            Log.e(TAG, "saveMatchToDb: " + timestamp + " -->> " + ts);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        MatchModel matchModel = new MatchModel(matchId, title, description, matchLocation, latitude + "", longitude + "", matchDate + " " + matchTime, timestamp, mAuth.getCurrentUser().getUid());
        mFirebaseDatabase.child("match").child(matchId).setValue(matchModel, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(@Nullable final DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    geoFire.setLocation(matchId, new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                            addUsertoEvent(matchId);

                            if (error != null) {
                                Log.e("ERROR: ", "There was an error saving the location to GeoFire: " + error);
                                System.err.println("There was an error saving the location to GeoFire: " + error);
                            } else {
                                Log.e("ERROR: ", "Location saved on server successfully! " + key);
                                System.out.println("Location saved on server successfully!");
                                Toast.makeText(CreateEventActivity.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            }
        });

    }

    //Save userevent in firebase
    private void addUsertoEvent(final String matchid) {

        mFirebaseDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final HashMap<String, Object> map1 = new HashMap<>();
                //final UserModel userModel = dataSnapshot.getValue(UserModel.class);

                /*List<String> matchList = new ArrayList<>();
                matchList.addAll(userModel.getMember());
                matchList.add(matchid);
                //map1.put("friends", userModel.getFriendid());
                map1.put("member", matchList);
                //map1.put("email", userModel.getEmail());
                Log.e(TAG, "onDataChange:===> " + userModel );*/
                List<String> matchList  = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String memberdata = dataSnapshot1.getValue(String.class);
                    matchList.add(memberdata);
                    Log.e(TAG, "onDataChange:===:> " + memberdata );
                }
               /* matchList.add("member");*/
                matchList.add(matchid);
                dataSnapshot.getRef().setValue(matchList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      /*  if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
*/
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               /* if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }*/
            }
        });

    }

    private void openTimePicker() {

        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String min = "";
                        if (minute < 10)
                            min = "0" + minute;
                        else
                            min = minute + "";

                        tvMatchTime.setText(hourOfDay + ":" + min);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void openDatePicker() {
//        getLocationFromAddress(edtMatchLocation.getText().toString().trim());
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String month = "";
                        String day = "";
                        if (monthOfYear < 10)
                            month = "0" + (monthOfYear + 1);
                        else
                            month = (monthOfYear + 1) + "";
                        if (dayOfMonth < 10)
                            day = "0" + dayOfMonth;
                        else
                            day = dayOfMonth + "";

                        tvMatchDate.setText(day + "-" + (month) + "-" + year);


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public LatLng getLocationFromAddress(String zipcode) {
        LatLng latLng = null;

        final Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(zipcode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Use the address as needed
                Log.e(TAG, "getLocationFromAddress: " + address.getLatitude() + "-->> " + address.getLongitude());
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
                return latLng;
            } else {
                Log.e(TAG, "getLocationFromAddress: unable ");
                // Display appropriate message when Geocoder services are not available
            }
        } catch (IOException e) {
            Log.e(TAG, "getLocationFromAddress: catch " + e.getMessage().toString());
            // handle exception
        }
        return latLng;


    }

    public void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
