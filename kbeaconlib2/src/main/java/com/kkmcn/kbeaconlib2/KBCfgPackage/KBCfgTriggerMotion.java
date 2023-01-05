package com.kkmcn.kbeaconlib2.KBCfgPackage;

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

    public int updateConfig(HashMap<String, Object> dicts)
    {
        int nUpdateParaNum = super.updateConfig(dicts);
        Integer nTempValue = null;

        nTempValue = (Integer)dicts.get(JSON_FIELD_TRIGGER_MOTION_ACC_ODR);
        if (nTempValue != null)
        {
            accODR = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer)dicts.get(JSON_FIELD_TRIGGER_MOTION_DURATION);
        if (nTempValue != null)
        {
            wakeupDuration = nTempValue;
            nUpdateParaNum++;
        }

        return nUpdateParaNum;
    }

    public HashMap<String, Object> toDictionary()
    {
        HashMap<String, Object>cfgDicts = super.toDictionary();
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
