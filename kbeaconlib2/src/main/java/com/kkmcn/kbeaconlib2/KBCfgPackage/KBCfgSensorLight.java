package com.kkmcn.kbeaconlib2.KBCfgPackage;

import org.json.JSONException;
import org.json.JSONObject;

public class KBCfgSensorLight extends KBCfgSensorBase{
    public static final String JSON_SENSOR_TYPE_LUX_LOG_ENABLE = "log";
    public static final String JSON_SENSOR_TYPE_LUX_MEASURE_INTERVAL = "msItvl";
    public static final String JSON_SENSOR_TYPE_LUX_CHANGE_THD = "luxThd";

    //measure interval
    public static final int DEFAULT_LUX_MEASURE_INTERVAL = 5;
    public static final int MAX_MEASURE_INTERVAL = 200;
    public static final int MIN_MEASURE_INTERVAL = 1;

    //light change threshold
    public static final int DEFAULT_LIGHT_CHANGE_LOG_THD = 20;
    public static final int MAX_LIGHT_CHANGE_LOG_THD = 65535;
    public static final int MIN_LIGHT_CHANGE_LOG_THD = 1;

    //log enable
    private Boolean logEnable;

    //measure interval
    private Integer measureInterval;

    //light change threshold
    private Integer logChangeThreshold;

    public KBCfgSensorLight()
    {
        super();
        sensorType = KBSensorType.Light;
    }

    public Boolean isLogEnable() {
        return logEnable;
    }

    public Integer getSensorType()
    {
        return sensorType;
    }

    public Integer getMeasureInterval()
    {
        return measureInterval;
    }

    public  Integer getLogChangeThreshold()
    {
        return logChangeThreshold;
    }

    //Enable light log enable
    public void setLogEnable(Boolean logEnable) {
        this.logEnable = logEnable;
    }

    public void setMeasureInterval(int measureInterval)
    {
        this.measureInterval = measureInterval;
    }

    public void setLogChangeThreshold(Integer lightChangeThreshold) {
        this.logChangeThreshold = lightChangeThreshold;
    }

    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateConfigNum = super.updateConfig(dicts);

        if (dicts.has(JSON_SENSOR_TYPE_LUX_LOG_ENABLE))
        {
            logEnable = (dicts.getInt(JSON_SENSOR_TYPE_LUX_LOG_ENABLE) > 0);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_LUX_MEASURE_INTERVAL))
        {
            measureInterval = dicts.getInt(JSON_SENSOR_TYPE_LUX_MEASURE_INTERVAL);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_LUX_CHANGE_THD))
        {
            logChangeThreshold = dicts.getInt(JSON_SENSOR_TYPE_LUX_CHANGE_THD);
            nUpdateConfigNum++;
        }

        return nUpdateConfigNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject configDicts = super.toJSONObject();

        if (logEnable != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_LUX_LOG_ENABLE, logEnable ? 1: 0);
        }

        if (measureInterval != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_LUX_MEASURE_INTERVAL, measureInterval);
        }

        if (logChangeThreshold != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_LUX_CHANGE_THD, logChangeThreshold);
        }

        return configDicts;
    }
}

