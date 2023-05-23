package com.kkmcn.kbeaconlib2.KBCfgPackage;

import org.json.JSONException;
import org.json.JSONObject;

public class KBCfgSensorPIR extends KBCfgSensorBase{
    public static final String JSON_SENSOR_TYPE_PIR_LOG_BACKOFF_TIME = "bkOff";

    //measure interval
    public static final int DEFAULT_PIR_MEASURE_INTERVAL = 1;
    public static final int MAX_MEASURE_INTERVAL = 200;
    public static final int MIN_MEASURE_INTERVAL = 1;

    //log backoff time
    public static final int DEFAULT_BACKOFF_TIME_SEC = 30;
    public static final int MAX_BACKOFF_TIME_SEC = 3600;
    public static final int MIN_BACKOFF_TIME_SEC = 5;

    //log enable
    private Boolean logEnable = null;

    //measure interval
    private Integer measureInterval = null;

    //log backoff time
    private Integer logBackoffTime = null;

    public KBCfgSensorPIR()
    {
        super();
        sensorType = KBSensorType.PIR;
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

    public  Integer getLogBackoffTime()
    {
        return logBackoffTime;
    }

    public void setLogEnable(Boolean logEnable) {
        this.logEnable = logEnable;
    }

    public boolean setMeasureInterval(int interval)
    {
        if (interval >= MIN_MEASURE_INTERVAL && interval <= MAX_MEASURE_INTERVAL) {
            measureInterval = interval;
            return true;
        } else {
            return false;
        }
    }

    public boolean setLogBackoffTime(Integer backoffTime) {
        if (backoffTime >= MIN_BACKOFF_TIME_SEC && backoffTime <= MAX_BACKOFF_TIME_SEC) {
            logBackoffTime = backoffTime;
            return true;
        }else{
            return false;
        }
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

        if (dicts.has(JSON_SENSOR_TYPE_PIR_LOG_BACKOFF_TIME))
        {
            logBackoffTime = dicts.getInt(JSON_SENSOR_TYPE_PIR_LOG_BACKOFF_TIME);
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

        if (logBackoffTime != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_PIR_LOG_BACKOFF_TIME, logBackoffTime);
        }

        return configDicts;
    }
}


