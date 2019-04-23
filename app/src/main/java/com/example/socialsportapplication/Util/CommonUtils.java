package com.example.socialsportapplication.Util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;




public class CommonUtils {


    Context mContext;



    public CommonUtils(Context mContext) {
        this.mContext = mContext;

    }

    //check internet connection
    public boolean isNetworkAvailable() {


        ConnectivityManager
                cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }




}
