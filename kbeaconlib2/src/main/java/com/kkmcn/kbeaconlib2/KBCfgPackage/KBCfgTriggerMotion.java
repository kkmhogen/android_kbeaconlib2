package com.kkmcn.kbeaconlib2.KBCfgPackage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class KBCfgTriggerMotion extends KBCfgTrigger{
    public final static int ACC_ODR_1_HZ = 0x0;
    public final static int ACC_ODR_10_HZ = 0x1;
    public final static int ACC_ODR_25_HZ = 0x2;
    public final static int ACC_ODR_50_HZ = 0x3;

    public final static int MIN_WAKEUP_DURATION = 1;
    public final static int MAX_WAKEUP_DURATION = 255;

    public final static int ACC_DEFAULT_ODR = 0x2;
    public final static int ACC_DEFAULT_WAKEUP_DURATION = 1;

    public static final String JSON_FIELD_TRIGGER_MOTION_ACC_ODR = "odr";
    public static final String JSON_FIELD_TRIGGER_MOTION_DURATION = "ocnt";

    protected Integer accODR;

    //the wakeup duration unit is 1/odr
    protected Integer wakeupDuration;

    public KBCfgTriggerMotion()
    {
        triggerType = KBTriggerType.AccMotion;
        triggerIndex = 0;
    }

    public boolean setAccODR(Integer odr) {
        if (odr < ACC_ODR_1_HZ || odr > ACC_ODR_50_HZ) {
            return false;
        }
        accODR = odr;
        return true;
    }

    public boolean setWakeupDuration(Integer duration) {
        if (duration < MIN_WAKEUP_DURATION || duration > MAX_WAKEUP_DURATION)
        {
            return false;
        }

        this.wakeupDuration = duration;
        return true;
    }

    public Integer getAccODR() {
        return accODR;
    }

    public Integer getWakeupDuration() {
        return wakeupDuration;
    }

    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateParaNum = super.updateConfig(dicts);

        if (dicts.has(JSON_FIELD_TRIGGER_MOTION_ACC_ODR))
        {
            accODR = dicts.getInt(JSON_FIELD_TRIGGER_MOTION_ACC_ODR);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_TRIGGER_MOTION_DURATION))
        {
            wakeupDuration = dicts.getInt(JSON_FIELD_TRIGGER_MOTION_DURATION);
            nUpdateParaNum++;
        }

        return nUpdateParaNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject cfgDicts = super.toJSONObject();
        if (accODR != null)
        {
            cfgDicts.put(JSON_FIELD_TRIGGER_MOTION_ACC_ODR, accODR);
        }

        if (wakeupDuration != null)
        {
            cfgDicts.put(JSON_FIELD_TRIGGER_MOTION_DURATION, wakeupDuration);
        }

        return cfgDicts;
    }
}
