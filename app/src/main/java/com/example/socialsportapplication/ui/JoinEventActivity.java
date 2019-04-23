package com.example.socialsportapplication.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialsportapplication.Firebase;
import com.example.socialsportapplication.Models.MatchModel;
import com.example.socialsportapplication.Models.UserModel;
import com.example.socialsportapplication.R;
import com.example.socialsportapplication.Util.CommonUtils;
import com.example.socialsportapplication.Util.Constants;
import com.example.socialsportapplication.adapter.EventsListAdapter;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//https://stackoverflow.com/questions/29711728/how-to-sort-geo-points-according-to-the-distance-from-current-location-in-androi
//https://www.youtube.com/watch?v=vHwToYEYvsU
//https://www.youtube.com/watch?v=NjTs4lMkRM4
//https://www.youtube.com/watch?v=EM2x33g4syY


public class JoinEventActivity extends AppCompatActivity {

    private static final String TAG = "JoinEventActivity";
    RecyclerView recycleEvents;
    AlertDialog.Builder builder;
    SeekBar seekbarMiles;
    EventsListAdapter eventsListAdapter;
    List<MatchModel> matchModelList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    GeoFire geoFire;
    double latitude = 0, longitude = 0;
    HashMap<String, String> requestedList = new HashMap<>();
    List<String> list = new ArrayList<>();
    CommonUtils commonUtils;
    @BindView(R.id.tvNoevent)
    TextView tvNoevent;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseAuth mAuth = Firebase.getInstance().auth();
    private String keyvalue;
    private Spinner spinnerMiles;
    private Location userLocation;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_event);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        recycleEvents = findViewById(R.id.recycle_events);
        spinnerMiles = findViewById(R.id.spinner_miles);
        progressDialog = new ProgressDialog(this);
        commonUtils = new CommonUtils(this);
        builder = new AlertDialog.Builder(this);
        progressDialog.setMessage("Loading...");

        if (!commonUtils.isNetworkAvailable()) {
            Toast.makeText(this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference("geolocation"));

        spinnerMiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemSelected: " + parent.getItemAtPosition(position).toString());
                matchModelList.clear();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                    eventsListAdapter.notifyDataSetChanged();
                    getEventListFromDb(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        userLocation = new Location("me");
        userLocation.setLatitude(latitude);
        userLocation.setLongitude(longitude);


        Log.e(TAG, "init: latlng " + latitude + "--->> " + longitude);
        linearLayoutManager = new LinearLayoutManager(JoinEventActivity.this, LinearLayoutManager.VERTICAL, false);
        recycleEvents.setLayoutManager(linearLayoutManager);
        eventsListAdapter = new EventsListAdapter(JoinEventActivity.this, matchModelList, mAuth.getCurrentUser().getUid(), new EventsListAdapter.ItemListener() {
            //TODO Request event click
            @Override
            public void onItemClick(MatchModel item) {
                Log.e(TAG, "onItemClick: " + item.getLatitude() + "--->>" + item.getLongitude());
                if (!item.getOwnerid().equals(mAuth.getCurrentUser().getUid())) {
                    RequestIntoEvent(item.getMatchid(), item.getOwnerid(), item.getU_Id(), item);
                } else {
                    Toast.makeText(JoinEventActivity.this, "You are the creator", Toast.LENGTH_SHORT).show();
                }


            }
        }, new EventsListAdapter.DetailListner() {
            //TODO Match Details activity click
            @Override
            public void onViewClick(MatchModel item) {
                Constants.matchModel = item;
                Intent intent1 = new Intent(JoinEventActivity.this, MatchDetailsActivity.class);
                intent1.putExtra(Constants.matchId, item.getMatchid());
                intent1.putExtra(Constants.ownerId, item.getOwnerid());
                intent1.putExtra(Constants.userId, item.getU_Id());
                intent1.putExtra(Constants.title, item.getName());
                intent1.putExtra(Constants.location, item.getAddress());
                intent1.putExtra(Constants.datetime, item.getDatetime());
                intent1.putExtra(Constants.desc, item.getDescription());
                intent1.putExtra(Constants.lat, item.getLatitude());
                intent1.putExtra(Constants.longi, item.getLongitude());
                intent1.putExtra(Constants.longi, item.getLongitude());
                startActivity(intent1);
            }
        });
        recycleEvents.setAdapter(eventsListAdapter);

    }


    private void friendEvent(final String matchId, final String ownerId) {
        progressDialog.show();
        mFirebaseDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                Log.e(TAG, "onDataChange:===> " + userModel.getEmail());
                long timeStamp = System.currentTimeMillis();
                String timeString = Long.toString(timeStamp);


                HashMap<String, Object> map = new HashMap<>();

                map.put("user_name", userModel.getEmail());
                map.put("user_id", mAuth.getCurrentUser().getUid());
                map.put("macth_id", matchId);
                map.put("owner_id", ownerId);
                map.put("status", false);


                mFirebaseDatabase.child("friends").child(matchId + "_" + mAuth.getCurrentUser().getUid())
                        .setValue(map, new DatabaseReference.CompletionListener() {

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            }
                        });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Log.e(TAG, "onCancelled:===> " + databaseError.getMessage());
            }
        });


    }


    private void RequestIntoEvent(final String matchid, final String ownerid, final String userId, final MatchModel item) {
        mFirebaseDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final HashMap<String, Object> map1 = new HashMap<>();
                final UserModel userModel = dataSnapshot.getValue(UserModel.class);
////                if (userModel.getMember() != null) {
//
//                    Log.e(TAG, "matchid: " + userModel.getMember() + "---->>>" + matchList.contains(matchid));
//                }
//                Log.e(TAG, "matchid:------ " + matchList.contains(matchid) + "--->>" + matchList.size());
                if (!userModel.getMember().contains(matchid)) {
                    if (!userModel.getJoin().contains(matchid)) {
//
                        List<String> matchList = new ArrayList<>();
                        matchList.addAll(userModel.getJoin());
                        matchList.add(matchid);
                        map1.put("join", matchList);
                        map1.put("email", userModel.getEmail());
                        map1.put("member", userModel.getMember());
                        dataSnapshot.getRef().setValue(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                friendEvent(matchid, ownerid);
                                Toast.makeText(JoinEventActivity.this, "Requested into event successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(JoinEventActivity.this, "You have already requested to join this event.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    builder.setMessage("Do You want to leave?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    mFirebaseDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("member").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                String matchvalue = dataSnapshot1.getValue(String.class);
                                                if (matchvalue.contains(matchid)) {
                                                    dataSnapshot1.getRef().removeValue();
                                                    //mFirebaseDatabase.child("users").child(ownerid).child("friends").child(matchid + "_" + mAuth.getCurrentUser().getUid()).removeValue();
                                                    HashMap<String, Object> map = new HashMap<>();
                                                    map.put("user_name", userModel.getEmail());
                                                    map.put("user_id", userId);
                                                    map.put("macth_id", matchid);
                                                    map.put("owner_id", ownerid);
                                                    map.put("status", false);
                                                    mFirebaseDatabase.child("friends").child(matchid + "_" + mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            item.setStatus(false);
                                                            Toast.makeText(JoinEventActivity.this, "You have successfully left this event.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();

                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("You are already a member!");
                    alert.show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });

    }


    private void getEventListFromDb(String miles) {
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), Double.parseDouble(miles));

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                // progressDialog.show();
                Log.e(TAG, "onKeyEntered: " + key + "-->> " + location);
                keyvalue = key;
                mFirebaseDatabase.child("match").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        recycleEvents.setVisibility(View.VISIBLE);
                        tvNoevent.setVisibility(View.GONE);
                        Log.e(TAG, "onDataChange:key== " + dataSnapshot.getKey());
                        Log.e(TAG, "onDataChange:value== " + dataSnapshot.getValue());
                        MatchModel matchModel = dataSnapshot.getValue(MatchModel.class);
                        Log.e(TAG, "onDataChange: " + matchModel.getName());
                        matchModelList.add(matchModel);
                        Log.e(TAG, "matchModelList: " + matchModelList.size());
                        Collections.sort(matchModelList, new Comparator<MatchModel>() {
                            @Override
                            public int compare(MatchModel u1, MatchModel u2) {

                                Location first = new Location("");
                                first.setLatitude(Double.parseDouble(u1.getLatitude()));
                                first.setLongitude(Double.parseDouble(u1.getLongitude()));

                                Location second = new Location("");
                                second.setLatitude(Double.parseDouble(u2.getLatitude()));
                                second.setLongitude(Double.parseDouble(u2.getLongitude()));

                                if (userLocation.distanceTo(first) > userLocation.distanceTo(second)) {
                                    return 1;
                                } else if (userLocation.distanceTo(first) < userLocation.distanceTo(second)) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        });

                        if (eventsListAdapter != null){
                                eventsListAdapter.notifyDataSetChanged();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onKeyExited(String key) {
                Log.e(TAG, "onKeyExited: ");

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d(TAG, "onKeyMoved() returned: ");
            }

            @Override
            public void onGeoQueryReady() {
                Log.d(TAG, "onGeoQueryReady() returned: ");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e(TAG, "onGeoQueryError: " + error.getMessage().toString());
            }
        });
    }
}
