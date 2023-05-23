package com.kkmcn.kbeaconlib2.KBCfgPackage;

import org.json.JSONException;
import org.json.JSONObject;

public class KBCfgSensorCO2 extends KBCfgSensorBase{
    public static final String JSON_SENSOR_TYPE_CO2_CHANGE_THD = "co2Thd";
    public static final String JSON_SENSOR_TYPE_CO2_ASC_ENABLE = "asc";

    //measure interval
    public static final int DEFAULT_CO2_MEASURE_INTERVAL = 300;
    public static final int MAX_CO2_MEASURE_INTERVAL = 3600;
    public static final int MIN_CO2_MEASURE_INTERVAL = 10;

    //co2 change threshold
    public static final int DEFAULT_CO2_CHANGE_LOG_THD = 20;
    public static final int MAX_CO2_CHANGE_LOG_THD = 256;
    public static final int MIN_CO2_CHANGE_LOG_THD = 5;


    //log enable
    private Boolean logEnable;

    //asc enable
    private Boolean ascEnable;

    //measure interval
    private Integer measureInterval;

    //voc change threshold
    private Integer logCO2SaveThreshold;

    public KBCfgSensorCO2()
    {
        super();

        sensorType = KBSensorType.Light;
    }


    public Integer getSensorType()
    {
        return sensorType;
    }

    public Boolean isLogEnable() {
        return logEnable;
    }

    public Boolean isAscEnable(){
        return ascEnable;
    }

    public void setAscEnable(Boolean ascEnable) {
        this.ascEnable = ascEnable;
    }

    public void setLogEnable(Boolean logEnable) {
        this.logEnable = logEnable;
    }

    public Integer getMeasureInterval()
    {
        return measureInterval;
    }

    public void setMeasureInterval(int measureInterval)
    {
        this.measureInterval = measureInterval;
    }

    public Integer getLogCO2SaveThreshold() {
        return logCO2SaveThreshold;
    }

    public void setLogCO2SaveThreshold(Integer logCO2SaveThreshold) {
        this.logCO2SaveThreshold = logCO2SaveThreshold;
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

        if (dicts.has(JSON_SENSOR_TYPE_CO2_CHANGE_THD))
        {
            logCO2SaveThreshold = (Integer) dicts.get(JSON_SENSOR_TYPE_CO2_CHANGE_THD);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_CO2_ASC_ENABLE))
        {
            ascEnable = (Integer) dicts.get(JSON_SENSOR_TYPE_CO2_ASC_ENABLE) > 0;
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

        if (logCO2SaveThreshold != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_CO2_CHANGE_THD, logCO2SaveThreshold);
        }

        if (ascEnable != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_CO2_ASC_ENABLE, ascEnable ? 1 : 0);
        }

        return configDicts;
    }
}

