package com.example.socialsportapplication.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.socialsportapplication.Models.MatchModel;

public class Constants {

    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    public static final String isSocialSportLogin = "is_socialSportlogin";

    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    public static String title = "title";
    public static String location = "location";
    public static String datetime = "datetime";
    public static String desc = "description";
    public static String lat = "latitude";
    public static String longi = "longitude";
    public static String matchId = "matchid";
    public static String ownerId = "ownerid";
    public static String userId = "userid";

    public  static MatchModel matchModel;


}
