package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;
import com.kkmcn.kbeaconlib2.KBException;
import com.kkmcn.kbeaconlib2.KBUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class KBCfgAdvEddyUID extends KBCfgAdvBase
{
    public final static String JSON_FIELD_EDDY_UID_NID  = "nid";
    public final static String JSON_FIELD_EDDY_UID_SID  = "sid";

    public final static String DEFAULT_NAMESPACE_ID = "0x00000000000000000001";
    public final static String DEFAULT_SERIALD_ID = "0x000000000001";
    public final static int NAMESPACE_ID_LENGTH = 22;
    public final static int SERIAL_ID_LENGTH = 14;

    private String nid;

    private String sid;

    public KBCfgAdvEddyUID()
    {
        super();
        advType = KBAdvType.EddyUID;
    }

    public String getNid()
    {
        return nid;
    }

    public String getSid()
    {
        return sid;
    }

    public boolean setNid(String strNid)
    {
        if (strNid.length() == 22 && KBUtility.isHexString(strNid))
        {
            nid = strNid;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean setSid(String strSid)  {
        if (strSid.length() == 14 && KBUtility.isHexString(strSid)) {
            sid = strSid;
            return true;
        } else {
            return false;
        }
    }


    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdatePara = super.updateConfig(dicts);

        if (dicts.has(JSON_FIELD_EDDY_UID_NID))
        {
            nid = dicts.getString(JSON_FIELD_EDDY_UID_NID);
            nUpdatePara++;
        }

        if (dicts.has(JSON_FIELD_EDDY_UID_SID))
        {
            sid = dicts.getString(JSON_FIELD_EDDY_UID_SID);
            nUpdatePara++;
        }

        return nUpdatePara;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject cfgDicts = super.toJSONObject();

        if (nid != null)
        {
            cfgDicts.put(JSON_FIELD_EDDY_UID_NID, nid);
        }

        if (sid != null)
        {
            cfgDicts.put(JSON_FIELD_EDDY_UID_SID, sid);
        }

        return cfgDicts;
    }
}
