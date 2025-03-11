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

    //    0 AES ECB
    public final static int  AES_ECB_TYPE = 1;
    public final static int  DEFAULT_ENCRYPT_INTERVAL= 0x05;
    public final static int  MIN_ENCRYPT_INTERVAL = 1;
    public final static int  MAX_ENCRYPT_INTERVAL = 100;

    public final static int DEFAULT_UUID_LENGTH = 36;

    //aes type, 0 : AES ECB
    private Integer aesType;

    //encrypt interval, unit is second
    private Integer enItvl;

    //UUID for encrypt
    private String uuid;

    public String getUuid()
    {
        return uuid;
    }

    public Integer getAesType()
    {
        return aesType;
    }

    public Integer getEncryptInterval()
    {
        return enItvl;
    }

    public KBCfgAdvEBeacon()
    {
        super();
        advType = KBAdvType.EBeacon;
    }

    //Set the AES KEY to change every interval seconds
    public boolean setEncryptInterval(Integer interval)
    {
        if (interval >= MIN_ENCRYPT_INTERVAL && interval <= MAX_ENCRYPT_INTERVAL)
        {
            enItvl = interval;
            return true;
        }
        else
        {
            return false;
        }
    }

    //set aes encrypt type, 0 : ECB
    public void setAesType(Integer type)
    {
        aesType = type;
    }

    //Beacon will encrypt UUID and broadcast it.
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
