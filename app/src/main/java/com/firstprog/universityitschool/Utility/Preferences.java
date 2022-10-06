package com.firstprog.universityitschool.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    //arbitrary classes
    private static final String LOGIN_DATA = "login_status", ROLE = "user_role", Uid = "user_id", UEmail = "user_email", UPass = "user_Pass";

    private static SharedPreferences getSharedReferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setRole(Context context, String role) {
        SharedPreferences.Editor editor = getSharedReferences(context).edit();
        editor.putString(ROLE, role);
        editor.apply();
    }

    public static String getRole(Context context) {
        return getSharedReferences(context).getString(ROLE, "");
    }

    public static void setUid(Context context, String uid) {
        SharedPreferences.Editor editor = getSharedReferences(context).edit();
        editor.putString(Uid, uid);
        editor.apply();
    }

    public static String getUid(Context context) {
        return getSharedReferences(context).getString(Uid, "");
    }

    public static void setUPass(Context context, String uPass) {
        SharedPreferences.Editor editor = getSharedReferences(context).edit();
        editor.putString(UPass, uPass);
        editor.apply();
    }

    public static String getUPass(Context context) {
        return getSharedReferences(context).getString(UPass, "");
    }

    public static void setUEmail(Context context, String uEmail) {
        SharedPreferences.Editor editor = getSharedReferences(context).edit();
        editor.putString(UEmail, uEmail);
        editor.apply();
    }

    public static String getUEmail(Context context) {
        return getSharedReferences(context).getString(UEmail, "");
    }

    public static void setDataLogin(Context context, Boolean status) {
        SharedPreferences.Editor editor = getSharedReferences(context).edit();
        editor.putBoolean(LOGIN_DATA, status);
        editor.apply();
    }

    public static Boolean getStatus(Context context) {
        return getSharedReferences(context).getBoolean(LOGIN_DATA, false);
    }

    public static void clearData(Context context) {
        SharedPreferences.Editor editor = getSharedReferences(context).edit();
        editor.remove(LOGIN_DATA);
        editor.remove(ROLE);
        editor.remove(Uid);
        editor.remove(UEmail);
        editor.remove(UPass);
        editor.apply();
    }
}
