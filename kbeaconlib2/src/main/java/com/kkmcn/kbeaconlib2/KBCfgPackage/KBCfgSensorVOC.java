package com.kkmcn.kbeaconlib2.KBCfgPackage;

import org.json.JSONException;
import org.json.JSONObject;

public class KBCfgSensorVOC  extends KBCfgSensorBase{
    public static final String JSON_SENSOR_TYPE_LOG_ENABLE = "log";
    public static final String JSON_SENSOR_TYPE_MEASURE_INTERVAL = "msItvl";
    public static final String JSON_SENSOR_TYPE_VOC_CHANGE_THD = "vocThd";
    public static final String JSON_SENSOR_TYPE_NOX_CHANGE_THD = "noxThd";

    //measure interval
    public static final int DEFAULT_VOC_MEASURE_INTERVAL = 10;
    public static final int MAX_VOC_MEASURE_INTERVAL = 200;
    public static final int MIN_VOC_MEASURE_INTERVAL = 3;

    //voc change threshold
    public static final int DEFAULT_VOC_CHANGE_LOG_THD = 20;
    public static final int MAX_VOC_CHANGE_LOG_THD = 250;
    public static final int MIN_VOC_CHANGE_LOG_THD = 1;

    //voc change threshold
    public static final int DEFAULT_NOX_CHANGE_LOG_THD = 1;
    public static final int MAX_NOX_CHANGE_LOG_THD = 250;
    public static final int MIN_NOX_CHANGE_LOG_THD = 1;

    //log enable
    private Boolean logEnable;

    //measure interval
    private Integer measureInterval;

    //voc change threshold
    private Integer logVocChangeThreshold;

    //nox change threshold
    private Integer logNoxChangeThreshold;

    public KBCfgSensorVOC()
    {
        super();

        sensorType = KBSensorType.VOC;
    }


    public Integer getSensorType()
    {
        return sensorType;
    }

    public Boolean isLogEnable() {
        return logEnable;
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

    public Integer getLogNoxChangeThreshold() {
        return logNoxChangeThreshold;
    }

    public void setLogNoxChangeThreshold(Integer logNoxChangeThreshold) {
        this.logNoxChangeThreshold = logNoxChangeThreshold;
    }

    public  Integer getVocLogChangeThreshold()
    {
        return logVocChangeThreshold;
    }

    public void setVocLogChangeThreshold(Integer vocChangeThreshold) {
        this.logVocChangeThreshold = vocChangeThreshold;
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
            measureInterval = dicts.getInt(JSON_SENSOR_TYPE_MEASURE_INTERVAL);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_VOC_CHANGE_THD))
        {
            logVocChangeThreshold = (Integer) dicts.get(JSON_SENSOR_TYPE_VOC_CHANGE_THD);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_NOX_CHANGE_THD))
        {
            logNoxChangeThreshold = (Integer) dicts.get(JSON_SENSOR_TYPE_NOX_CHANGE_THD);
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

        if (logVocChangeThreshold != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_VOC_CHANGE_THD, logVocChangeThreshold);
        }

        if (logNoxChangeThreshold != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_NOX_CHANGE_THD, logNoxChangeThreshold);
        }

        return configDicts;
    }
}

