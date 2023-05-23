package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;
import com.kkmcn.kbeaconlib2.KBException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class KBCfgAdvEddyURL extends KBCfgAdvBase{

    public static final String JSON_FIELD_EDDY_URL_ADDR  = "url";
    public static final String DEFAULT_URL_ADDRESS = "https://www.google.com/";
    public static int MAX_URL_LENGTH = 30;

    private String url;

    public KBCfgAdvEddyURL()
    {
        super();
        advType = KBAdvType.EddyURL;
    }

    public String getUrl()
    {
        return url;
    }

    public boolean setUrl(String  strUrl)
    {
        strUrl = strUrl.replace(" ", "");
        if (strUrl.length() >= 3)
        {
            url = strUrl;
            return true;
        }
        else
        {
            return false;
        }
    }

    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateParaNum = super.updateConfig(dicts);

        if (dicts.has(JSON_FIELD_EDDY_URL_ADDR))
        {
            url = (String)dicts.get(JSON_FIELD_EDDY_URL_ADDR);
            nUpdateParaNum++;
        }

        return nUpdateParaNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject cfgDicts = super.toJSONObject();
        if (url != null)
        {
            cfgDicts.put(JSON_FIELD_EDDY_URL_ADDR, url);
        }

        return cfgDicts;
    }
}
