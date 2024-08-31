package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;
import com.kkmcn.kbeaconlib2.KBUtility;

import org.json.JSONException;
import org.json.JSONObject;

public class KBCfgAdvEBeacon extends KBCfgAdvBase{
    public final static String JSON_FIELD_EBEACON_UUID  = "uuid";
    public final static String JSON_FIELD_EBEACON_AES_TYPE  = "aes";
    public final static String JSON_FIELD_EBEACON_INTERVAL = "enItvl";

    public final static String DEFAULT_UUID = "7777772E-6B6B-6D63-6E2E-636F6D000001";

    //    0 AES ECB（目前只有一种）
    public final static int  AES_ECB_TYPE = 0;
    public final static int  DEFAULT_INTERVER = 0x05;
    public final static int  MIN_INTERVAL = 1;
    public final static int  MAX_INTERVAL = 100;
    public final static int DEFAULT_UUID_LENGTH = 36;

    private Integer aesType;

    private Integer enItvl;

    private String uuid;

    public String getUuid()
    {
        return uuid;
    }

    public Integer getAesType()
    {
        return aesType;
    }

    public Integer getEnItvl()
    {
        return enItvl;
    }

    public KBCfgAdvEBeacon()
    {
        super();
        advType = KBAdvType.EBeacon;
    }

    public boolean setInterval(Integer interval)
    {
        if (interval >= MIN_INTERVAL && interval <= MAX_INTERVAL)
        {
            enItvl = interval;
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setAesType(Integer type)
    {
       aesType = type;
    }

    public boolean  setUuid(String strUuid)
    {
        if (KBUtility.isUUIDString(strUuid))
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

        if (dicts.has(JSON_FIELD_EBEACON_UUID))
        {
            uuid = (String)dicts.get(JSON_FIELD_EBEACON_UUID);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_EBEACON_AES_TYPE))
        {
            aesType = (Integer)dicts.get(JSON_FIELD_EBEACON_AES_TYPE);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_EBEACON_INTERVAL))
        {
            enItvl = (Integer)dicts.get(JSON_FIELD_EBEACON_INTERVAL);
            nUpdateParaNum++;
        }

        return nUpdateParaNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject cfgDicts = super.toJSONObject();

        if (uuid != null)
        {
            cfgDicts.put(JSON_FIELD_EBEACON_UUID, uuid);
        }

        if (aesType != null)
        {
            cfgDicts.put(JSON_FIELD_EBEACON_AES_TYPE, aesType);
        }

        if (enItvl != null)
        {
            cfgDicts.put(JSON_FIELD_EBEACON_INTERVAL, enItvl);
        }

        return cfgDicts;
    }
}
