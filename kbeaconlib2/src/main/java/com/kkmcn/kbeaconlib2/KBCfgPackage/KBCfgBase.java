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


    public int updateConfig(JSONObject dicts) throws JSONException
    {
        return 0;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        return new JSONObject();
    }

    protected KBCfgBase(){
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

}
