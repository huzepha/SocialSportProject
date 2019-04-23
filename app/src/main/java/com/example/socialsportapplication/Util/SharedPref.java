package com.example.socialsportapplication.Util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SharedPref {

	Context mContext;

	SharedPreferences mSettingPrefs;
    
	SharedPreferences.Editor mSettingPrefEditor;

	public SharedPref(Context mContext) {
		this.mContext = mContext;

		// Get the xml/configuration_activity.xml preferences
		mSettingPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	}


	public void setDataInPref(String mKey, String mItem) {
		mSettingPrefEditor = mSettingPrefs.edit();
		mSettingPrefEditor.putString(mKey, mItem);
		mSettingPrefEditor.commit();
	}


	public String getDataFromPref(String mKey) {
		String mSplashData = mSettingPrefs.getString(mKey, "");
		return mSplashData;
	}


	public void setInt(String mKey, int mItem) {
		mSettingPrefEditor = mSettingPrefs.edit();
		mSettingPrefEditor.putInt(mKey, mItem);
		mSettingPrefEditor.commit();
	}


	public int getInt(String mKey) {
		int mPos = mSettingPrefs.getInt(mKey, 0);
		return mPos;
	}


	public void clearAllPref() {
		mSettingPrefEditor = mSettingPrefs.edit();
		mSettingPrefEditor.clear();
		mSettingPrefEditor.commit();
	}


	public void setBoolean(String mKey, boolean mItem) {
		mSettingPrefEditor = mSettingPrefs.edit();
		mSettingPrefEditor.putBoolean(mKey, mItem);
		mSettingPrefEditor.commit();
	}


	public boolean getBoolean(String mKeys) {
		boolean mPos = mSettingPrefs.getBoolean(mKeys, false);
		return mPos;
	}

	public void setArrayPref(String key, ArrayList<String> values) {

		mSettingPrefEditor = mSettingPrefs.edit();
		JSONArray a = new JSONArray();
		for (int i = 0; i < values.size(); i++) {
			a.put(values.get(i));
		}
		if (!values.isEmpty()) {
			mSettingPrefEditor.putString(key, a.toString());
		} else {
			mSettingPrefEditor.putString(key, null);
		}
		mSettingPrefEditor.commit();
	}


}
