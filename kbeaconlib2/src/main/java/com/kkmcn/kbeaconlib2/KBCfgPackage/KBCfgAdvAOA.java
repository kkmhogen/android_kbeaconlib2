package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;

import org.json.JSONException;
import org.json.JSONObject;
public class KBCfgAdvAOA extends KBCfgAdvBase{

    public static final String JSON_FIELD_AOA_TYPE  = "aoa";
    public static final String JSON_FIELD_AOA_AXIS  = "axis";
    public static final String JSON_FIELD_AOA_SYS_ADV_INTERVAL  = "aItvl";
    public static final int FIELD_AOA_MAX_SYS_ADV_INTERVAL = 255;

    private Integer type;

    private Boolean axisSupport;

    //the beacon will advertisement system info(acc,version)
    // every sysAdvInterval when broadcasting AOA advertisement
    private Integer sysAdvInterval;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getAxisSupport() {
        return axisSupport;
    }

    public void setAxisSupport(Boolean axisSupport) {
        this.axisSupport = axisSupport;
    }

    public Integer getSysAdvInterval() {
        return sysAdvInterval;
    }

    public void setSysAdvInterval(Integer aItvl) {
        this.sysAdvInterval = aItvl;
    }

    public KBCfgAdvAOA()
    {
        super();
        advType = KBAdvType.AOA;
    }

    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateParaNum = super.updateConfig(dicts);

        if (dicts.has(JSON_FIELD_AOA_TYPE))
        {
            type = (Integer) dicts.get(JSON_FIELD_AOA_TYPE);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_AOA_AXIS))
        {
            axisSupport = (Integer)(dicts.get(JSON_FIELD_AOA_AXIS)) > 0;
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_AOA_SYS_ADV_INTERVAL))
        {
            sysAdvInterval = (Integer) dicts.get(JSON_FIELD_AOA_SYS_ADV_INTERVAL);
            nUpdateParaNum++;
        }

        return nUpdateParaNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject cfgDicts = super.toJSONObject();
        if (type != null)
        {
            cfgDicts.put(JSON_FIELD_AOA_TYPE, type);
        }

        if (axisSupport != null)
        {
            cfgDicts.put(JSON_FIELD_AOA_AXIS, axisSupport?1:0);
        }

        if (sysAdvInterval != null)
        {
            cfgDicts.put(JSON_FIELD_AOA_SYS_ADV_INTERVAL, sysAdvInterval);
        }

        return cfgDicts;
    }
}
