package com.example.appolis.physicalinventory;

import android.content.SharedPreferences;

/**
 * Created by Jeffery on 10/2/2014.
 */
public class SharedPrefs {

    public static SharedPreferences Sharedprefs;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String spUrl = "urlKey";
    public static final String spDomain = "domainKey";
    public static final String spUsername = "userKey";
    public static final String spPassword = "passKey";
    public static final String spCompany = "companyKey";
    public static final String spVerified = "verifiedKey";
    public static final String spSite = "siteKey";

    public static SharedPreferences.Editor ed = Sharedprefs.edit();

    public static void SavePreference(String key, String value){
        ed.putString(key, value);
        ed.apply();
    }

    public static void GetPreference(String key){
       Sharedprefs.getString(key, "");
    }




}
