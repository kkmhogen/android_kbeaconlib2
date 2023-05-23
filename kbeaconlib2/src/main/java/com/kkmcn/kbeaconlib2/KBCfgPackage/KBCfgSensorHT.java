package com.kkmcn.kbeaconlib2.KBCfgPackage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class KBCfgSensorHT extends KBCfgSensorBase{
    public static final String JSON_SENSOR_TYPE_HT_TEMP_CHANGE_THD = "tsThd";
    public static final String JSON_SENSOR_TYPE_HT_HUMIDITY_CHANGE_THD = "hsThd";

    //measure interval
    public static final int DEFAULT_HT_MEASURE_INTERVAL = 3;
    public static final int MAX_MEASURE_INTERVAL = 200;
    public static final int MIN_MEASURE_INTERVAL = 1;

    //temperature change threshold
    public static final int DEFAULT_HT_TEMP_CHANGE_THD = 5;   //0.1 Celsius
    public static final int MAX_HT_TEMP_CHANGE_LOG_THD = 200;  //max value is 20 Celsius
    public static final int MIN_HT_TEMP_CHANGE_LOG_THD = 0;

    //humidity change threshold
    public static final int DEFAULT_HT_HUMIDITY_CHANGE_THD = 30;  //unit is 0.1%
    public static final int MAX_HT_HUMIDITY_CHANGE_LOG_THD = 200;  //max value is 20%
    public static final int MIN_HT_HUMIDITY_CHANGE_LOG_THD = 0;

    //log enable
    private Boolean logEnable;

    //measure interval
    private Integer sensorHtMeasureInterval;

    //temperature interval
    private Integer temperatureChangeThreshold;

    //humidity interval
    private Integer humidityChangeThreshold;

    public KBCfgSensorHT()
    {
        super();
        sensorType =KBSensorType.HTHumidity;
    }

    public Boolean isLogEnable() {
        return logEnable;
    }

    public Integer getSensorType()
    {
        return sensorType;
    }

    public Integer getSensorHtMeasureInterval()
    {
        return sensorHtMeasureInterval;
    }

    public  Integer getTemperatureChangeThreshold()
    {
        return temperatureChangeThreshold;
    }

    public Integer getHumidityChangeThreshold()
    {
        return humidityChangeThreshold;
    }

    //Enable temperature and humidity Log feature
    public void setLogEnable(Boolean logEnable) {
        this.logEnable = logEnable;
    }

    //Temperature and humidity measure interval, unit is second,
    public void setSensorHtMeasureInterval(Integer nMeasureInterval)
    {
        sensorHtMeasureInterval = nMeasureInterval;
    }

    //Temperature log threshold, unit is 0.1 Celsius,
    // for example, if nSaveThd = 5, then if abs(current temperature - last saved temperature) > 0.5, then device will save new record
    public void setTemperatureChangeThreshold(Integer nSaveThd)
    {
        temperatureChangeThreshold = nSaveThd;
    }

    //Humidity log threshold, unit is 0.1%
    //for example, if nSaveThd = 50, then if abs(current humidity - last saved humidity) > 5, then device will save new record
    public void setHumidityChangeThreshold(Integer nSaveThd)
    {
        humidityChangeThreshold = nSaveThd;
    }


    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateConfigNum = super.updateConfig(dicts);
        Object obj;

        if (dicts.has(JSON_SENSOR_TYPE_LOG_ENABLE))
        {
            logEnable = (dicts.getInt(JSON_SENSOR_TYPE_LOG_ENABLE) > 0);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_MEASURE_INTERVAL))
        {
            sensorHtMeasureInterval = (Integer) dicts.get(JSON_SENSOR_TYPE_MEASURE_INTERVAL);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_HT_TEMP_CHANGE_THD))
        {
            temperatureChangeThreshold = (Integer) dicts.get(JSON_SENSOR_TYPE_HT_TEMP_CHANGE_THD);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_HT_HUMIDITY_CHANGE_THD))
        {
            humidityChangeThreshold = (Integer) dicts.get(JSON_SENSOR_TYPE_HT_HUMIDITY_CHANGE_THD);
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

        if (sensorHtMeasureInterval != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_MEASURE_INTERVAL, sensorHtMeasureInterval);
        }

        if (temperatureChangeThreshold != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_HT_TEMP_CHANGE_THD, temperatureChangeThreshold);
        }

        if (humidityChangeThreshold != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_HT_HUMIDITY_CHANGE_THD, humidityChangeThreshold);
        }

        return configDicts;
    }

}
