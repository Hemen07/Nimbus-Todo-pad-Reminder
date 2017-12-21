package com.redfox.nimbustodo.data.preferences.common_pref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.redfox.nimbustodo.util.common_util.UtilCommonConstants;

public class SPCommonMgr {
    private final static String TAG = SPCommonMgr.class.getSimpleName();
    private static final boolean LOG_DEBUG = false;

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @SuppressLint("CommitPrefEdits")
    public SPCommonMgr(Context context) {
        this.context = context.getApplicationContext();
        sharedPreferences = context.getSharedPreferences(UtilCommonConstants.C_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (LOG_DEBUG)
            Log.i(TAG, "ctor");
    }

    //NoteUpdateActivity : Tooltip
    public void saveStatusToolTip(int status) {
        editor.putInt(UtilCommonConstants.TOOLTIP, status);
        editor.commit();
    }


    public Integer getSavedStatusToolTip() {
        return sharedPreferences.getInt(UtilCommonConstants.TOOLTIP, 0);
    }

    //MainActivity ToolTip
    public void saveStatusToolTipMA(int status) {
        editor.putInt(UtilCommonConstants.TOOLTIP_MA, status);
        editor.commit();
    }


    public Integer getSavedStatusToolTipMA() {
        return sharedPreferences.getInt(UtilCommonConstants.TOOLTIP_MA, 0);
    }

    //Google SignIn :
    public void saveSignInStatus(int status) {
        editor.putInt(UtilCommonConstants.IS_SIGNED_IN, status);
        editor.apply();
    }

    public Integer getSignInStatus() {
        return sharedPreferences.getInt(UtilCommonConstants.IS_SIGNED_IN, 0);
    }

    //Name and Email
    public void saveSignInName(String name) {
        editor.putString(UtilCommonConstants.IS_SIGNED_NAME, name);
        editor.apply();
    }

    public String getSignInName() {
        return sharedPreferences.getString(UtilCommonConstants.IS_SIGNED_NAME, "GUEST");
    }

    public void saveSignInEmail(String name) {
        editor.putString(UtilCommonConstants.IS_SIGNED_EMAIL, name);
        editor.apply();
    }

    public String getSignInEmail() {
        return sharedPreferences.getString(UtilCommonConstants.IS_SIGNED_EMAIL, "");
    }

    //fragOne
    public void saveIntroCardVisibility(int status) {
        editor.putInt(UtilCommonConstants.INTRO_CARD_1, status);
        editor.apply();
    }

    public Integer getIntroCardVisibility() {
        return sharedPreferences.getInt(UtilCommonConstants.INTRO_CARD_1, 0);
    }

    //fragTwo
    public void saveIntroCardVisibility2(int status) {
        editor.putInt(UtilCommonConstants.INTRO_CARD_2, status);
        editor.apply();
    }

    public Integer getIntroCardVisibility2() {
        return sharedPreferences.getInt(UtilCommonConstants.INTRO_CARD_2, 0);
    }


}
