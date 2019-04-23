package com.example.socialsportapplication.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialsportapplication.Firebase;
import com.example.socialsportapplication.Models.MatchModel;
import com.example.socialsportapplication.Models.UserModel;
import com.example.socialsportapplication.R;
import com.example.socialsportapplication.Util.CommonUtils;
import com.example.socialsportapplication.Util.Constants;
import com.example.socialsportapplication.adapter.UpcomingPastEventAdapter;
import com.example.socialsportapplication.ui.MatchDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
//References used:
//https://www.youtube.com/watch?v=a4o9zFfyIM4

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    RecyclerView recycleListEvents;
    TextView tvUpcomingEvent, tvPastEvent;
    UpcomingPastEventAdapter upcomingPastEventAdapter;
    List<MatchModel> eventList = new ArrayList<>();
    CommonUtils commonUtils;
    @BindView(R.id.tvNonoti)
    TextView tvNonoti;
    Unbinder unbinder;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseAuth mAuth = Firebase.getInstance().auth();
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        commonUtils = new CommonUtils(getContext());
        if (!commonUtils.isNetworkAvailable()) {
            Toast.makeText(getContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        recycleListEvents = view.findViewById(R.id.recycle_list_event);
        tvUpcomingEvent = view.findViewById(R.id.tv_upcoming_event);
        tvPastEvent = view.findViewById(R.id.tv_past_event);

        bindRecycleviewData();

        init("upcomingevent");
        tvUpcomingEvent.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvPastEvent.setTextColor(getResources().getColor(R.color.colorblack));
        tvUpcomingEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventList.clear();
                tvUpcomingEvent.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvPastEvent.setTextColor(getResources().getColor(R.color.colorblack));
                init("upcomingevent");
            }
        });

        tvPastEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventList.clear();
                tvPastEvent.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvUpcomingEvent.setTextColor(getResources().getColor(R.color.colorblack));
                init("pastevent");
            }
        });

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void bindRecycleviewData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycleListEvents.setLayoutManager(linearLayoutManager);
        upcomingPastEventAdapter = new UpcomingPastEventAdapter(getActivity(), eventList, new UpcomingPastEventAdapter.ItemListener() {
            @Override
            public void onItemClick(MatchModel item) {
                Intent intent1 = new Intent(getContext(), MatchDetailsActivity.class);
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
        recycleListEvents.setAdapter(upcomingPastEventAdapter);
    }


    private void init(final String eventType) {

        Log.e(TAG, "init: " + mAuth.getCurrentUser().getUid());
        mFirebaseDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);

                Log.e(TAG, "onDataChange:==>event1 " + dataSnapshot);
                Log.e(TAG, "timestamp onDataChange: " + userModel.getMember() + "--->>" + userModel.getEmail());

                List<String> matchListId = userModel.getMember();
                matchListId.removeAll(Collections.singleton(null));

                Log.e(TAG, "timestamp onDataChange:size " + matchListId.size());
                for (int i = 0; i < matchListId.size(); i++) {
                    if (!matchListId.get(i).equals("member")) {
                        Long tsLong = System.currentTimeMillis() / 1000;
                        final String ts = tsLong.toString();
                        Log.e(TAG, "timestamp: " + matchListId.get(i) + " --->> " + ts);
                        mFirebaseDatabase.child("match").child(matchListId.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                MatchModel matchModel = dataSnapshot.getValue(MatchModel.class);
                                Log.e(TAG, "onDataChange:==>event2 " + dataSnapshot);
                                if (eventType.equals("pastevent")) {
                                    if (Long.valueOf(matchModel.getTimestamp()) < Long.valueOf(ts)) {
                                        Log.e(TAG, "timestamp onDataChange: trueeeeeeeeeee ");
                                        if (tvNonoti!=null){
                                            tvNonoti.setVisibility(View.GONE);
                                        }
                                        eventList.add(matchModel);
                                    }
                                } else if (eventType.equals("upcomingevent")) {
                                    if (Long.valueOf(matchModel.getTimestamp()) > Long.valueOf(ts)) {
                                        Log.e(TAG, "timestamp onDataChange: trueeeeeeeeeee ");
                                        if (tvNonoti!=null){
                                            tvNonoti.setVisibility(View.GONE);
                                        }
                                        eventList.add(matchModel);
                                    }
                                }
                                upcomingPastEventAdapter.notifyDataSetChanged();


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        if (tvNonoti!=null){
                            tvNonoti.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
