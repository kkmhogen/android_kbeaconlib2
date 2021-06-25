package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;
import com.kkmcn.kbeaconlib2.KBException;
import com.kkmcn.kbeaconlib2.KBUtility;

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

    public void setMajorID(Integer nMajorID) throws KBException
    {
        if (nMajorID >= 0 && nMajorID <= MAX_MAJOR_MINOR_VALUE)
        {
            majorID = nMajorID;
        }
        else
        {
            throw new KBException(KBException.KBEvtCfgInputInvalid, "majorID invalid");
        }
    }

    public void setMinorID(Integer nMinorID) throws KBException
    {
        if (nMinorID >= 0 && nMinorID <= MAX_MAJOR_MINOR_VALUE)
        {
            minorID = nMinorID;
        }
        else
        {
            throw new KBException(KBException.KBEvtCfgInputInvalid, "minorID invalid");
        }
    }

    public void  setUuid(String strUuid) throws KBException
    {
        if (KBUtility.isUUIDString(strUuid) )
        {
            uuid = strUuid;
        }
        else
        {
            throw new KBException(KBException.KBEvtCfgInputInvalid, "uuid invalid");
        }
    }

    public int updateConfig(HashMap<String, Object> dicts)
    {
        int nUpdateParaNum = super.updateConfig(dicts);

        String strTempValue = (String)dicts.get(JSON_FIELD_IBEACON_UUID);
        if (strTempValue != null)
        {
            uuid = strTempValue;
            nUpdateParaNum++;
        }

        Integer nTempValue = (Integer)dicts.get(JSON_FIELD_IBEACON_MAJORID);
        if (nTempValue != null)
        {
            majorID = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer)dicts.get(JSON_FIELD_IBEACON_MINORID);
        if (nTempValue != null)
        {
            minorID = nTempValue;
            nUpdateParaNum++;
        }

        return nUpdateParaNum;
    }

    public HashMap<String, Object> toDictionary()
    {
        HashMap<String, Object>cfgDicts = super.toDictionary();

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
