package com.kkmcn.kbeaconlib2.KBCfgPackage;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class KBCfgBase{

    private final static String LOG_TAG = "KBCfgBase";

    public final static String JSON_MSG_TYPE_KEY = "msg";
    public final static String JSON_MSG_TYPE_CFG = "cfg";
    public final static String JSON_MSG_TYPE_GET_PARA = "getPara";
    public final static String JSON_FIELD_SUBTYPE = "type";


    public int updateConfig(HashMap<String, Object> dicts)
    {
        return 0;
    }

    public HashMap<String, Object> toDictionary()
    {
        return new HashMap<>(10);
    }

    protected KBCfgBase(){
    }

    public JSONObject toJsonObject()
    {
        HashMap<String, Object> paraMap = toDictionary();
        JSONObject jo = new JSONObject();
        for (Map.Entry<String, Object> entry : paraMap.entrySet()) {
            try {
                jo.put((String)entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jo;
    }

    public static void JsonString2HashMap(String  strJsonMsg, Map<String, Object> rstList) {
        JSONObject mRspJason;
        try
        {
            mRspJason = new JSONObject(strJsonMsg);
            JsonObject2HashMap(mRspJason, rstList);
        }
        catch(JSONException excp)
        {
            Log.e(LOG_TAG, "Parse Jason network command response failed");
        }
    }

    public static void JsonObject2HashMap(JSONObject jo, Map<String, Object> rstList) {
        for (Iterator<String> keys = jo.keys(); keys.hasNext();) {
            try {
                String key1 = keys.next();
                rstList.put(key1, jo.get(key1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void HashMap2JsonObject( Map<String, Object> paraMap, JSONObject jo) {
        for (Map.Entry<String, Object> entry : paraMap.entrySet()) {
            try {
                jo.put((String)entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static String HashMap2JsonString( Map<String, Object> paraMap) {
        JSONObject jsonObj = new JSONObject();
        KBCfgBase.HashMap2JsonObject(paraMap, jsonObj);
        if (jsonObj.length() > 0){
            return jsonObj.toString();
        }else{
            return null;
        }
    }

    public Long parseLong(Object nData)
    {
        Long parseData = null;
        if (nData != null) {
            if (nData instanceof Integer) {
                parseData = ((Integer) nData).longValue();
            } else if (nData instanceof Long) {
                parseData = (Long)nData;
            }
        }

        return parseData;
    }

    public Float parseFloat(Object oPeriodData)
    {
        Float parseData = null;
        if (oPeriodData != null) {
            if (oPeriodData instanceof Float) {
                parseData = (Float) oPeriodData;
            } else if (oPeriodData instanceof Double) {
                Double nPeriodFlt = (Double) oPeriodData;
                parseData = (float) nPeriodFlt.doubleValue();
            } else if (oPeriodData instanceof Integer) {
                Integer nPeriodInt = (Integer) oPeriodData;
                parseData = (float) nPeriodInt;
            }
        }

        return parseData;
    }
}
