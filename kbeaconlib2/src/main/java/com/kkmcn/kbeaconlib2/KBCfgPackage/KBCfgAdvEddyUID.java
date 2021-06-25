package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;
import com.kkmcn.kbeaconlib2.KBException;
import com.kkmcn.kbeaconlib2.KBUtility;

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

    public void setNid(String strNid) throws KBException
    {
        if (strNid.length() == 22 && KBUtility.isHexString(strNid))
        {
            nid = strNid;
        }
        else
        {
            throw new KBException(KBException.KBEvtCfgInputInvalid, "nid invalid");
        }
    }

    public void setSid(String strSid) throws KBException {
        if (strSid.length() == 14 && KBUtility.isHexString(strSid)) {
            sid = strSid;
        } else {
            throw new KBException(KBException.KBEvtCfgInputInvalid, "sid invalid");
        }
    }


    public int updateConfig(HashMap<String, Object>dicts)
    {
        int nUpdatePara = super.updateConfig(dicts);

        if (dicts.get(JSON_FIELD_EDDY_UID_NID) != null)
        {
            nid = (String)dicts.get(JSON_FIELD_EDDY_UID_NID);
            nUpdatePara++;
        }

        if (dicts.get(JSON_FIELD_EDDY_UID_SID) != null)
        {
            sid = (String)dicts.get(JSON_FIELD_EDDY_UID_SID);
            nUpdatePara++;
        }

        return nUpdatePara;
    }

    public HashMap<String, Object> toDictionary()
    {
        HashMap<String, Object> cfgDicts = super.toDictionary();

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
