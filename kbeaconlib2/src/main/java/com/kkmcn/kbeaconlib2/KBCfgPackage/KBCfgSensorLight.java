package com.kkmcn.kbeaconlib2.KBCfgPackage;

import org.json.JSONException;
import org.json.JSONObject;

public class KBCfgSensorLight extends KBCfgSensorBase{
    public static final String JSON_SENSOR_TYPE_LUX_CHANGE_THD = "luxThd";

    //measure interval
    public static final int DEFAULT_LUX_MEASURE_INTERVAL = 5;
    public static final int MAX_MEASURE_INTERVAL = 200;
    public static final int MIN_MEASURE_INTERVAL = 1;

    //measure interval
    public static final int DEFAULT_LOG_INTERVAL = 300;
    public static final int MAX_LOG_INTERVAL = 14400;
    public static final int MIN_LOG_INTERVAL = 1;

    //light change threshold
    public static final int DEFAULT_LIGHT_CHANGE_LOG_THD = 20;
    public static final int MAX_LIGHT_CHANGE_LOG_THD = 200;
    public static final int MIN_LIGHT_CHANGE_LOG_THD = 0;

    //log enable
    private Boolean logEnable;

    //measure interval
    private Integer measureInterval;

    //log interval
    private Integer logInterval;

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

    public Integer getLogInterval()
    {
        return logInterval;
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

    //Light log interval, unit is second,
    public boolean setLogInterval(Integer nLogInterval)
    {
        if (nLogInterval >= MIN_LOG_INTERVAL && nLogInterval <= MAX_LOG_INTERVAL) {
            logInterval = nLogInterval;
            return true;
        } else {
            return false;
        }
    }

    public void setLogChangeThreshold(Integer lightChangeThreshold) {
        this.logChangeThreshold = lightChangeThreshold;
    }

    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateConfigNum = super.updateConfig(dicts);

        if (dicts.has(JSON_SENSOR_TYPE_LOG_ENABLE))
        {
            logEnable = (dicts.getInt(JSON_SENSOR_TYPE_LOG_ENABLE) > 0);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_MEASURE_INTERVAL))
        {
            measureInterval = dicts.getInt(JSON_SENSOR_TYPE_MEASURE_INTERVAL);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_LOG_INTERVAL))
        {
            logInterval = dicts.getInt(JSON_SENSOR_TYPE_LOG_INTERVAL);
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
            configDicts.put(JSON_SENSOR_TYPE_LOG_ENABLE, logEnable ? 1: 0);
        }

        if (measureInterval != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_MEASURE_INTERVAL, measureInterval);
        }

        if (logInterval != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_LOG_INTERVAL, logInterval);
        }

        if (logChangeThreshold != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_LUX_CHANGE_THD, logChangeThreshold);
        }

        return configDicts;
    }
}

