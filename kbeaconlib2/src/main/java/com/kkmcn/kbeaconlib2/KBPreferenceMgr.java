package com.kkmcn.kbeaconlib2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hogen on 15/9/22.
 */
class KBPreferenceMgr {
    private Context mContext;
    private final static String SETTING_INFO = "SETTING_INFO";
    private final static String FLD_VRY_CODE_NUM_VALUE = "VRY_CODE_NUM_VALUE";
    private final static String DEF_VRY_CODE_NUM = "0000000000000000";
    private String mDefaultPassword = DEF_VRY_CODE_NUM;


    @SuppressLint("StaticFieldLeak")
    private static KBPreferenceMgr sPrefMgr = null;

    public static synchronized KBPreferenceMgr shareInstance(Context ctx){
        if (sPrefMgr == null){
            sPrefMgr = new KBPreferenceMgr();
            sPrefMgr.initSetting(ctx);
        }

        return sPrefMgr;
    }

    private KBPreferenceMgr(){
    }

    public void initSetting(Context ctx){
        mContext = ctx;

        SharedPreferences shareReference = mContext.getSharedPreferences(SETTING_INFO, Context.MODE_PRIVATE);

        mDefaultPassword = shareReference.getString(FLD_VRY_CODE_NUM_VALUE, DEF_VRY_CODE_NUM);
    }

    public void setSingleBeaconPassword(String strMacAddress, String strVryCode){
        SharedPreferences shareReference = mContext.getSharedPreferences(SETTING_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shareReference.edit();
        edit.putString(FLD_VRY_CODE_NUM_VALUE+strMacAddress.toLowerCase(), strVryCode);
        edit.apply();
    }

    public String getSingleBeaconPassword(String strMacAddress){
        String strPassword;

        SharedPreferences shareReference = mContext.getSharedPreferences(SETTING_INFO, Context.MODE_PRIVATE);
        strPassword = shareReference.getString(FLD_VRY_CODE_NUM_VALUE+strMacAddress.toLowerCase(), null);
        if (strPassword == null){
            strPassword = mDefaultPassword;
        }

        return strPassword;
    }
}
