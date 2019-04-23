package com.example.socialsportapplication.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialsportapplication.Firebase;
import com.example.socialsportapplication.Models.MatchModel;
import com.example.socialsportapplication.Models.UserModel;
import com.example.socialsportapplication.R;
import com.example.socialsportapplication.Util.CommonUtils;
import com.example.socialsportapplication.Util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MatchDetailsActivity extends AppCompatActivity {

    private static final String TAG = "MatchDetailsActivity";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_date_time)
    TextView tvDateTime;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.btn_join)
    Button btnJoin;
    @BindView(R.id.btn_direction)
    Button btnDirection;
    @BindView(R.id.btnRequest)
    Button btnRequest;
    @BindView(R.id.btnleave)
    Button btnleave;
    /*@BindView(R.id.map)
    SupportMapFragment map;*/
    private DatabaseReference mFirebaseDatabase;
    private FirebaseAuth mAuth = Firebase.getInstance().auth();
    String lat, longi, matchId,ownerId,userId,title;
    CommonUtils commonUtils;
    AlertDialog.Builder builder;
    boolean isMember = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        commonUtils = new CommonUtils(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        builder = new AlertDialog.Builder(this);
        final Intent intent = getIntent();
        title = intent.getStringExtra(Constants.title);
        tvTitle.setText(title);
        tvLocation.setText(intent.getStringExtra(Constants.location));
        tvDescription.setText(intent.getStringExtra(Constants.desc));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            Date date = sdf.parse(intent.getStringExtra(Constants.datetime));
            SimpleDateFormat sdf4 = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
            tvDateTime.setText(sdf4.format(date));
        }catch (Exception e){
            Log.e(TAG, "setData:==>" + e.getMessage() );
        }
        matchId = intent.getStringExtra(Constants.matchId);
        ownerId = intent.getStringExtra(Constants.ownerId);
        userId = intent.getStringExtra(Constants.userId);
        lat = intent.getStringExtra(Constants.lat);
        longi = intent.getStringExtra(Constants.longi);

        Log.e(TAG, "onCreate:===> " + lat + " " + longi);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(Double.parseDouble(lat), Double.parseDouble(longi)))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(longi)))
                        .title(intent.getStringExtra(Constants.title)));


            }
        });


    }


    private void joinIntoEvent(final String matchid) {

        mFirebaseDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final HashMap<String, Object> map1 = new HashMap<>();
                UserModel userModel = dataSnapshot.getValue(UserModel.class);

                if (!userModel.getMember().contains(matchid)) {
                    List<String> matchList = new ArrayList<>();
                    matchList.addAll(userModel.getMember());
                    matchList.add(matchid);
                    map1.put("member", matchList);
                    map1.put("email", userModel.getEmail());
                    dataSnapshot.getRef().setValue(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MatchDetailsActivity.this, "Joined into event successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(MatchDetailsActivity.this, "You have already joined", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @OnClick({R.id.btn_join, R.id.btn_direction,R.id.btnRequest,R.id.btnleave})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_join:
                joinIntoEvent(matchId);
                break;
            case R.id.btn_direction:
                if (commonUtils.isNetworkAvailable()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + longi));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnRequest:
                if (!ownerId.equals(mAuth.getCurrentUser().getUid())) {
                    RequestEvent(matchId, ownerId);
                }else {
                    Toast.makeText(MatchDetailsActivity.this, "You are the creator", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnleave:
                if (!ownerId.equals(mAuth.getCurrentUser().getUid())) {
                    LeaveEvent(matchId, ownerId,userId);
                }else {
                    Toast.makeText(MatchDetailsActivity.this, "You are the creator", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    //TODO Request event method
    void RequestEvent(final String matchId, final String ownerId){
        mFirebaseDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final HashMap<String, Object> map1 = new HashMap<>();
                final UserModel userModel = dataSnapshot.getValue(UserModel.class);

                if (!userModel.getMember().contains(matchId)) {
                    if (!userModel.getJoin().contains(matchId)) {
//
                        List<String> matchList = new ArrayList<>();
                        matchList.addAll(userModel.getJoin());
                        matchList.add(matchId);
                        map1.put("join", matchList);
                        map1.put("email", userModel.getEmail());
                        map1.put("member", userModel.getMember());
                        dataSnapshot.getRef().setValue(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                friendEvent(matchId, ownerId);
                                Toast.makeText(MatchDetailsActivity.this, "You have requested to join", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(MatchDetailsActivity.this, "You have already requested to join", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //TODO Leave event method
    void LeaveEvent(final String matchId, final String ownerId, final String userId){

        mFirebaseDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String matchvalue = dataSnapshot1.getValue(String.class);
                    if (matchvalue.contains(matchId)) {
                        isMember = true;
                        dataSnapshot1.getRef().removeValue();
                        //mFirebaseDatabase.child("users").child(ownerId).child("friends").child(matchId + "_" + mAuth.getCurrentUser().getUid()).removeValue();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("user_name", title);
                        map.put("user_id", userId);
                        map.put("macth_id", matchId);
                        map.put("owner_id",ownerId);
                        map.put("status", false);
                        mFirebaseDatabase.child("friends").child(matchId+"_"+mAuth.getCurrentUser().getUid()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Constants.matchModel.setStatus(false);

                                Toast.makeText(MatchDetailsActivity.this, "You have successfully left this event.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
                if (!isMember){
                    Toast.makeText(MatchDetailsActivity.this, "You are not a member yet." , Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled:==> " + databaseError.getMessage() );
            }
        });
    }

    //TODO Update friend to firebase
    private void friendEvent(final String matchId, final String ownerId) {
       // progressDialog.show();
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
                map.put("owner_id",ownerId);
                map.put("status", false);


                mFirebaseDatabase.child("friends").child(matchId + "_" + mAuth.getCurrentUser().getUid())
                        .setValue(map, new DatabaseReference.CompletionListener() {

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            }
                        });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e(TAG, "onCancelled:===> " + databaseError.getMessage());
            }
        });


    }
}
