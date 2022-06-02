package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.UTCTime;

import java.util.HashMap;

public class KBCfgSensorBase extends KBCfgBase{
    public static final String JSON_SENSOR_TYPE = "srType";
    public static final String JSON_SENSOR_DISABLE_PERIOD0 = "dPrd0";
    public static final String JSON_SENSOR_DISABLE_PERIOD1 = "dPrd1";
    public static final String JSON_SENSOR_DISABLE_PERIOD2 = "dPrd2";

    //sensor type
    protected Integer sensorType;

    private Integer disablePeriod0;

    private Integer disablePeriod1;

    private Integer disablePeriod2;


    public void setSensorType(Integer sensorType) {
        this.sensorType = sensorType;
    }

    public Integer getSensorType() {
        return sensorType;
    }

    public KBTimeRange getDisablePeriod0() {
        return new KBTimeRange(disablePeriod0);
    }

    public KBTimeRange getDisablePeriod1() {
        return new KBTimeRange(disablePeriod1);
    }

    public KBTimeRange getDisablePeriod2() {
        return new KBTimeRange(disablePeriod2);
    }

    public void setDisablePeriod0(KBTimeRange sleepTime) {
        this.disablePeriod0 = sleepTime.toUTCInteger();
    }

    public void setDisablePeriod1(KBTimeRange sleepTime) {
        this.disablePeriod1 = sleepTime.toUTCInteger();
    }

    public void setDisablePeriod2(KBTimeRange sleepTime) {
        this.disablePeriod2 = sleepTime.toUTCInteger();
    }

    public int updateConfig(HashMap<String,Object> dicts)
    {
        int nUpdateConfigNum = super.updateConfig(dicts);
        Object obj;

        obj = dicts.get(JSON_SENSOR_TYPE);
        if (obj != null)
        {
            sensorType = (Integer) obj;
            nUpdateConfigNum++;
        }

        obj = dicts.get(JSON_SENSOR_DISABLE_PERIOD0);
        if (obj != null)
        {
            disablePeriod0 = (Integer) obj;
            nUpdateConfigNum++;
        }

        obj = dicts.get(JSON_SENSOR_DISABLE_PERIOD1);
        if (obj != null)
        {
            disablePeriod1 = (Integer) obj;
            nUpdateConfigNum++;
        }

        obj = dicts.get(JSON_SENSOR_DISABLE_PERIOD2);
        if (obj != null)
        {
            disablePeriod2 = (Integer) obj;
            nUpdateConfigNum++;
        }

        return nUpdateConfigNum;
    }

    public HashMap<String, Object> toDictionary() {
        HashMap<String, Object> configDicts = super.toDictionary();
        if (sensorType != null)
        {
            configDicts.put(JSON_SENSOR_TYPE, sensorType);
        }

        if (disablePeriod0 != null)
        {
            configDicts.put(JSON_SENSOR_DISABLE_PERIOD0, disablePeriod0);
        }
        if (disablePeriod1 != null)
        {
            configDicts.put(JSON_SENSOR_DISABLE_PERIOD1, disablePeriod1);
        }
        if (disablePeriod2 != null)
        {
            configDicts.put(JSON_SENSOR_DISABLE_PERIOD2, disablePeriod2);
        }
        return configDicts;
    }
}
