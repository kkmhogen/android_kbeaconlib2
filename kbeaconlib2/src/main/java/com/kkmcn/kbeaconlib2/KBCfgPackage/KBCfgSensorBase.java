package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.UTCTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class KBCfgSensorBase extends KBCfgBase{
    public static final String JSON_SENSOR_TYPE = "srType";
    public static final String JSON_SENSOR_DISABLE_PERIOD0 = "dPrd0";
    public static final String JSON_SENSOR_DISABLE_PERIOD1 = "dPrd1";
    public static final String JSON_SENSOR_DISABLE_PERIOD2 = "dPrd2";

    public static final String JSON_SENSOR_TYPE_LOG_ENABLE = "log";
    public static final String JSON_SENSOR_TYPE_MEASURE_INTERVAL = "msItvl";

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
        if (disablePeriod0 == null){
            return null;
        }
        return new KBTimeRange(disablePeriod0);
    }

    public KBTimeRange getDisablePeriod1() {
        if (disablePeriod1 == null){
            return null;
        }

        return new KBTimeRange(disablePeriod1);
    }

    public KBTimeRange getDisablePeriod2() {
        if (disablePeriod2 == null){
            return null;
        }
        return new KBTimeRange(disablePeriod2);
    }

    public void setDisablePeriod0(KBTimeRange period) {
        this.disablePeriod0 = period.toUTCInteger();
    }

    public void setDisablePeriod1(KBTimeRange period) {
        this.disablePeriod1 = period.toUTCInteger();
    }

    public void setDisablePeriod2(KBTimeRange period) {
        this.disablePeriod2 = period.toUTCInteger();
    }

    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateConfigNum = super.updateConfig(dicts);
        Object obj;

        if (dicts.has(JSON_SENSOR_TYPE))
        {
            sensorType = (Integer) dicts.get(JSON_SENSOR_TYPE);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_DISABLE_PERIOD0))
        {
            disablePeriod0 = (Integer) dicts.get(JSON_SENSOR_DISABLE_PERIOD0);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_DISABLE_PERIOD1))
        {
            disablePeriod1 = (Integer) dicts.get(JSON_SENSOR_DISABLE_PERIOD1);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_DISABLE_PERIOD2))
        {
            disablePeriod2 = (Integer) dicts.get(JSON_SENSOR_DISABLE_PERIOD2);
            nUpdateConfigNum++;
        }

        return nUpdateConfigNum;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject configDicts = super.toJSONObject();
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
