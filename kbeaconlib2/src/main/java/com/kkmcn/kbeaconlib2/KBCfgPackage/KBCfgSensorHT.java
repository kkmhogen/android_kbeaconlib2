package com.kkmcn.kbeaconlib2.KBCfgPackage;

import java.util.HashMap;

public class KBCfgSensorHT extends KBCfgSensorBase{
    public static final String JSON_SENSOR_TYPE_HT_LOG_ENABLE = "log";
    public static final String JSON_SENSOR_TYPE_HT_MEASURE_INTERVAL = "msItvl";
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


    public int updateConfig(HashMap<String,Object> dicts)
    {
        int nUpdateConfigNum = super.updateConfig(dicts);
        Object obj;

        obj = dicts.get(JSON_SENSOR_TYPE_HT_LOG_ENABLE);
        if (obj != null)
        {
            logEnable = ((Integer) obj > 0);
            nUpdateConfigNum++;
        }

        obj = dicts.get(JSON_SENSOR_TYPE_HT_MEASURE_INTERVAL);
        if (obj != null)
        {
            sensorHtMeasureInterval = (Integer) obj;
            nUpdateConfigNum++;
        }

        obj = dicts.get(JSON_SENSOR_TYPE_HT_TEMP_CHANGE_THD);
        if (obj != null)
        {
            temperatureChangeThreshold = (Integer) obj;
            nUpdateConfigNum++;
        }

        obj = dicts.get(JSON_SENSOR_TYPE_HT_HUMIDITY_CHANGE_THD);
        if (obj != null)
        {
            humidityChangeThreshold = (Integer) obj;
            nUpdateConfigNum++;
        }

        return nUpdateConfigNum;
    }

    public HashMap<String, Object> toDictionary()
    {
        HashMap<String, Object> configDicts = super.toDictionary();

        if (logEnable != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_HT_LOG_ENABLE, logEnable ? 1: 0);
        }

        if (sensorHtMeasureInterval != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_HT_MEASURE_INTERVAL, sensorHtMeasureInterval);
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
