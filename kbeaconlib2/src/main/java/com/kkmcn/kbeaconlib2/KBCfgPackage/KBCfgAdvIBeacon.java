package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;
import com.kkmcn.kbeaconlib2.KBException;
import com.kkmcn.kbeaconlib2.KBUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class KBCfgAdvIBeacon extends KBCfgAdvBase{
    public final static String JSON_FIELD_IBEACON_UUID  = "uuid";
    public final static String JSON_FIELD_IBEACON_MAJORID  = "majorID";
    public final static String JSON_FIELD_IBEACON_MINORID = "minorID";

    public final static String DEFAULT_UUID = "7777772E-6B6B-6D63-6E2E-636F6D000001";
    public final static int DEFAULT_MAJOR = 0x1;
    public final static int DEFAULT_MINOR = 0x1;

    public final static int MAX_MAJOR_MINOR_VALUE = 65535;
    public final static int DEFAULT_UUID_LENGTH = 36;

    private Integer majorID;

    private Integer minorID;

    private String uuid;

    public String getUuid()
    {
        return uuid;
    }

    public Integer getMajorID()
    {
        return majorID;
    }

    public Integer getMinorID()
    {
        return minorID;
    }

    public KBCfgAdvIBeacon()
    {
        super();
        advType = KBAdvType.IBeacon;
    }

    public boolean setMajorID(Integer nMajorID)
    {
        if (nMajorID >= 0 && nMajorID <= MAX_MAJOR_MINOR_VALUE)
        {
            majorID = nMajorID;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean setMinorID(Integer nMinorID)
    {
        if (nMinorID >= 0 && nMinorID <= MAX_MAJOR_MINOR_VALUE)
        {
            minorID = nMinorID;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean  setUuid(String strUuid)
    {
        if (KBUtility.isUUIDString(strUuid) )
        {
            uuid = strUuid;
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

        if (dicts.has(JSON_FIELD_IBEACON_UUID))
        {
            uuid = (String)dicts.get(JSON_FIELD_IBEACON_UUID);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_IBEACON_MAJORID))
        {
            majorID = (Integer)dicts.get(JSON_FIELD_IBEACON_MAJORID);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_IBEACON_MINORID))
        {
            minorID = (Integer)dicts.get(JSON_FIELD_IBEACON_MINORID);
            nUpdateParaNum++;
        }

        return nUpdateParaNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject cfgDicts = super.toJSONObject();

        if (uuid != null)
        {
            cfgDicts.put(JSON_FIELD_IBEACON_UUID, uuid);
        }

        if (majorID != null)
        {
            cfgDicts.put(JSON_FIELD_IBEACON_MAJORID, majorID);
        }

        if (minorID != null)
        {
            cfgDicts.put(JSON_FIELD_IBEACON_MINORID, minorID);
        }

        return cfgDicts;
    }
}
