package com.kkmcn.kbeaconlib2.KBCfgPackage;

import org.json.JSONException;
import org.json.JSONObject;

public class KBCfgSensorNFC extends KBCfgSensorBase{
    public static final String JSON_SENSOR_NFC_URI_ID= "id";
    public static final String JSON_SENSOR_NFC_URI_CONTENT = "uri";

    //max uri id value
    public static final int NFC_URI_EMPTY = 0;
    public static final int NFC_URI_HTTP_WWW = 1;
    public static final int NFC_URI_HTTPS_WWW = 2;
    public static final int NFC_URI_HTTP = 3;
    public static final int NFC_URI_HTTPS = 4;
    public static final int NFC_URI_TEL = 5;
    public static final int NFC_URI_MAILTO = 6;

    public static final int MAX_NFC_URI_CONTENT_LEN = 256;


    private Integer uriID;

    private String uriContent;

    public KBCfgSensorNFC()
    {
        super();
        sensorType = KBSensorType.NFC;
    }

    public  Integer getUriID()
    {
        return uriID;
    }

    //Enable light log enable
    public void setUriID(Integer id) {
        this.uriID = id;
    }

    public void setUriContent(String content)
    {
        this.uriContent = content;
    }


    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateConfigNum = super.updateConfig(dicts);

        if (dicts.has(JSON_SENSOR_NFC_URI_ID))
        {
            uriID = dicts.getInt(JSON_SENSOR_NFC_URI_ID);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_NFC_URI_CONTENT))
        {
            uriContent = dicts.getString(JSON_SENSOR_NFC_URI_CONTENT);
            nUpdateConfigNum++;
        }

        return nUpdateConfigNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject configDicts = super.toJSONObject();

        if (uriID != null)
        {
            configDicts.put(JSON_SENSOR_NFC_URI_ID, uriID);
        }

        if (uriContent != null)
        {
            configDicts.put(JSON_SENSOR_NFC_URI_CONTENT, uriContent);
        }

        return configDicts;
    }
}
