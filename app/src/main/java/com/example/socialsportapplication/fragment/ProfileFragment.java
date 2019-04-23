package com.example.socialsportapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.socialsportapplication.R;
import com.example.socialsportapplication.Util.Constants;
import com.example.socialsportapplication.Util.SharedPref;
import com.example.socialsportapplication.ui.HomeActivity;
import com.example.socialsportapplication.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    Button btnLogout;
    SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPref = new SharedPref(getActivity());
        btnLogout = view.findViewById(R.id.btn_logout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set login to false to manage app side login
                sharedPref.setBoolean(Constants.isSocialSportLogin, false);
                //signout from firebase
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
                //intent page to main login screen
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}
