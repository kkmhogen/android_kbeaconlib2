package com.kkmcn.kbeaconlib2.KBCfgPackage;

import java.util.HashMap;

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

    public int updateConfig(HashMap<String,Object> dicts)
    {
        int nUpdateConfigNum = super.updateConfig(dicts);
        Object obj;

        obj = dicts.get(JSON_SENSOR_TYPE_LUX_LOG_ENABLE);
        if (obj != null)
        {
            logEnable = ((Integer) obj > 0);
            nUpdateConfigNum++;
        }

        obj = dicts.get(JSON_SENSOR_TYPE_LUX_MEASURE_INTERVAL);
        if (obj != null)
        {
            measureInterval = (Integer) obj;
            nUpdateConfigNum++;
        }

        obj = dicts.get(JSON_SENSOR_TYPE_LUX_CHANGE_THD);
        if (obj != null)
        {
            logChangeThreshold = (Integer) obj;
            nUpdateConfigNum++;
        }

        return nUpdateConfigNum;
    }

    public HashMap<String, Object> toDictionary()
    {
        HashMap<String, Object> configDicts = super.toDictionary();

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

