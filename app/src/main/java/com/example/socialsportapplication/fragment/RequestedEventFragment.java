package com.example.socialsportapplication.fragment;

import android.app.ProgressDialog;
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
import com.example.socialsportapplication.Models.RequestEventModel;
import com.example.socialsportapplication.Models.UserModel;
import com.example.socialsportapplication.R;
import com.example.socialsportapplication.Util.CommonUtils;
import com.example.socialsportapplication.adapter.RequestedEventAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RequestedEventFragment extends Fragment {

    private static final String TAG = "RequestedEventFragment";
    RecyclerView recycleRequestedEvents;
    RequestedEventAdapter requestedEventAdapter;
    List<MatchModel> eventList = new ArrayList<>();
    int removePos;
    @BindView(R.id.tvNonoti)
    TextView tvNonoti;
    Unbinder unbinder;
    CommonUtils commonUtils;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseAuth mAuth = Firebase.getInstance().auth();
    private String email;
    private String id;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requested_event, container, false);
        recycleRequestedEvents = view.findViewById(R.id.recycle_requested_event);
        init();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void init() {
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        commonUtils = new CommonUtils(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        if (!commonUtils.isNetworkAvailable()) {
            Toast.makeText(getContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
        bindRecycleviewData();
        //getRequestedList();
        getRequsestedUserList();
    }


    void getRequsestedUserList() {
        mFirebaseDatabase.child("friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final String userId = dataSnapshot.child("user_id").getValue(String.class);
                final String user_name = dataSnapshot.child("user_name").getValue(String.class);
                String match_id = dataSnapshot.child("macth_id").getValue(String.class);
                final String owner_id = dataSnapshot.child("owner_id").getValue(String.class);
                final boolean status = dataSnapshot.child("status").getValue(Boolean.class);

                mFirebaseDatabase.child("match").child(match_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MatchModel matchModel = dataSnapshot.getValue(MatchModel.class);
                        matchModel.setU_Id(userId);
                        matchModel.setUser_name(user_name);
                        matchModel.setStatus(status);
                        Log.e(TAG, "onDataChange:---->id1 " + status);
                        eventList.add(matchModel);


                        if (!mAuth.getCurrentUser().getUid().equals(owner_id) && !mAuth.getCurrentUser().getUid().equals(userId)) {
                            if (tvNonoti != null) {
                                tvNonoti.setVisibility(View.VISIBLE);
                            }
                        }else {
                            if (tvNonoti != null) {
                                tvNonoti.setVisibility(View.GONE);
                            }
                        }



                        if (requestedEventAdapter != null) {
                            requestedEventAdapter.notifyDataSetChanged();
                        }

                        Log.e(TAG, "onChildAdded:");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG, "onChildChanged:");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "onChildRemoved:");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void bindRecycleviewData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycleRequestedEvents.setLayoutManager(linearLayoutManager);

        requestedEventAdapter = new RequestedEventAdapter(getActivity(), eventList, new RequestedEventAdapter.ItemListener() {
            @Override
            //TODO Accept user event

            public void onItemClick(final MatchModel item, int pos) {
                //removePos = pos;
                acceptEvent(item.getMatchid(), item.getU_Id(), item);

            }

        }, new RequestedEventAdapter.DeclineListner() {
            @Override
            //TODO Decline user event
            public void onDeclineClick(MatchModel item, int position) {
                removePos = position;
                declineEvent(item.getMatchid(), item.getU_Id());
            }
        });
        recycleRequestedEvents.setAdapter(requestedEventAdapter);
    }

    //TODO Declineevent method
    private void declineEvent(final String matchId, final String userId) {
        mFirebaseDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFirebaseDatabase.child(userId).child("join").orderByValue().equalTo(matchId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();
                        mFirebaseDatabase.child("friends").child(matchId + "_" + userId).removeValue();
                        requestedEventAdapter.removeAt(removePos);
                        Toast.makeText(getContext(), "Decline", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //TODO acceptevent method
    private void acceptEvent(final String matchid, final String userId, final MatchModel item) {

        mFirebaseDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final HashMap<String, Object> map1 = new HashMap<>();
                final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                List<String> matchList = new ArrayList<>();
                matchList.addAll(userModel.getMember());
                matchList.add(matchid);
                map1.put("member", matchList);
                map1.put("email", userModel.getEmail());
                dataSnapshot.getRef().setValue(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mFirebaseDatabase.child(userId).child("join").orderByValue().equalTo(matchid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dataSnapshot.getRef().removeValue();
                                Log.e(TAG, "onComplete:=-===> " + userModel.getEmail() + " " + matchid + " " + userId);
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("user_name", userModel.getEmail());
                                map.put("user_id", userId);
                                map.put("macth_id", matchid);
                                map.put("owner_id", item.getOwnerid());
                                map.put("status", true);
                                mFirebaseDatabase.child("friends").child(matchid + "_" + userId).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        item.setStatus(true);
                                        requestedEventAdapter.notifyDataSetChanged();
                                        Toast.makeText(getActivity(), "Event join successful", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                });


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
