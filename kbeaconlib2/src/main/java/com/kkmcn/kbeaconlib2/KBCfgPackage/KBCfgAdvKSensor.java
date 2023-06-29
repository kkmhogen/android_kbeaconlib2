package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class KBCfgAdvKSensor extends KBCfgAdvBase{
    public static final String JSON_FIELD_SENSOR_HUMIDITY = "ht";
    public static final String JSON_FIELD_SENSOR_AXIS= "axis";
    public static final String JSON_FIELD_SENSOR_LUX = "lux";
    public static final String JSON_FIELD_SENSOR_PIR = "pir";
    public static final String JSON_FIELD_SENSOR_VOC = "voc";
    public static final String JSON_FIELD_SENSOR_CO2 = "co2";
    public static final String JSON_FIELD_RECORD_COUNT = "rcd";

    private Boolean htSensorInclude;
    private Boolean axisSensorInclude;
    private Boolean lightSensorInclude;
    private Boolean pirSensorInclude;
    private Boolean vocSensorInclude;
    private Boolean co2SensorInclude;
    private Boolean recordInclude;

    public KBCfgAdvKSensor()
    {
        super();
        advType = KBAdvType.Sensor;
    }

    public void setAxisSensorInclude(Boolean axisInclude) {
        this.axisSensorInclude = axisInclude;
    }

    public void setLightSensorInclude(Boolean lightSensorInclude) {
        this.lightSensorInclude = lightSensorInclude;
    }

    public void setPirSensorInclude(Boolean pirSensorInclude) {
        this.pirSensorInclude = pirSensorInclude;
    }

    public Boolean isAxisSensorEnable() {
        return axisSensorInclude;
    }

    public void setHtSensorInclude(Boolean htSensorInclude) {
        this.htSensorInclude = htSensorInclude;
    }

    public Boolean isHtSensorInclude() {
        return htSensorInclude;
    }

    public Boolean isLightSensorInclude() {
        return lightSensorInclude;
    }

    public Boolean isPIRSensorInclude() {
        return pirSensorInclude;
    }

    public void setVocSensorInclude(Boolean vocSensorInclude) {
        this.vocSensorInclude = vocSensorInclude;
    }

    public Boolean isVocSensorInclude(){
        return vocSensorInclude;
    }

    public void setCo2SensorInclude(Boolean co2SensorInclude) {
        this.co2SensorInclude = co2SensorInclude;
    }

    public Boolean isCo2SensorInclude(){
        return co2SensorInclude;
    }

    public void setRecordInclude(Boolean recordInclude) {
        this.recordInclude = recordInclude;
    }

    public Boolean isRecordInclude(){
        return recordInclude;
    }

    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateConfigNum = super.updateConfig(dicts);

        if (dicts.has(JSON_FIELD_SENSOR_HUMIDITY))
        {
            htSensorInclude = (dicts.getInt(JSON_FIELD_SENSOR_HUMIDITY) > 0);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_FIELD_SENSOR_AXIS))
        {
            axisSensorInclude = (dicts.getInt(JSON_FIELD_SENSOR_AXIS) > 0);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_FIELD_SENSOR_LUX))
        {
            lightSensorInclude = (dicts.getInt(JSON_FIELD_SENSOR_LUX) > 0);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_FIELD_SENSOR_PIR))
        {
            pirSensorInclude = (dicts.getInt(JSON_FIELD_SENSOR_PIR) > 0);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_FIELD_SENSOR_VOC))
        {
            vocSensorInclude = (dicts.getInt(JSON_FIELD_SENSOR_VOC) > 0);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_FIELD_SENSOR_CO2))
        {
            co2SensorInclude = (dicts.getInt(JSON_FIELD_SENSOR_CO2) > 0);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_FIELD_RECORD_COUNT))
        {
            recordInclude = (dicts.getInt(JSON_FIELD_RECORD_COUNT) > 0);
            nUpdateConfigNum++;
        }

        return nUpdateConfigNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject configDicts = super.toJSONObject();

        if (htSensorInclude != null)
        {
            configDicts.put(JSON_FIELD_SENSOR_HUMIDITY, htSensorInclude ? 1 : 0);
        }

        if (axisSensorInclude != null)
        {
            configDicts.put(JSON_FIELD_SENSOR_AXIS, axisSensorInclude ? 1: 0);
        }

        if (lightSensorInclude != null)
        {
            configDicts.put(JSON_FIELD_SENSOR_LUX, lightSensorInclude ? 1: 0);
        }

        if (pirSensorInclude != null)
        {
            configDicts.put(JSON_FIELD_SENSOR_PIR, pirSensorInclude ? 1: 0);
        }

        if (vocSensorInclude != null)
        {
            configDicts.put(JSON_FIELD_SENSOR_VOC, vocSensorInclude ? 1: 0);
        }

        if (co2SensorInclude != null)
        {
            configDicts.put(JSON_FIELD_SENSOR_CO2, co2SensorInclude ? 1: 0);
        }

        if (recordInclude != null)
        {
            configDicts.put(JSON_FIELD_RECORD_COUNT, recordInclude ? 1: 0);
        }

        return configDicts;
    }
}
