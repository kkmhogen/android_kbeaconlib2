package com.kkmcn.kbeaconlib2.KBCfgPackage;

import org.json.JSONException;
import org.json.JSONObject;

public class KBCfgSensorGEO extends KBCfgSensorBase{
    public static final String JSON_SENSOR_TYPE_GEO_PTHD = "pThd";
    public static final String JSON_SENSOR_TYPE_GEO_PDLY = "pDly";
    public static final String JSON_SENSOR_TYPE_GEO_TAG = "tag";
    public static final String JSON_SENSOR_TYPE_GEO_FCL = "fcl";

    public static final int MAX_PARKING_CHANGE_THD = 65535;
    public static final int MIN_PARKING_CHANGE_THD = 256;
    public static final int DEFAULT_PARKING_CHANGE_THD = 1000;

    public static final int MAX_PARKING_DELAY_THD = 100;
    public static final int MIN_PARKING_DELAY_THD = 1;
    public static final int DEFAULT_PARKING_DELAY_THD = 9;

    //measure interval
    private Integer measureInterval;

    //parking GEO sensor change threshold
    private Integer parkingThreshold;

    //parking delay
    private Integer parkingDelay;

    //idle parking tag
    private Integer parkingTag;

    //force GEO sensor calibration
    private Integer calibration;

    public KBCfgSensorGEO()
    {
        super();

        sensorType = KBSensorType.GEO;
    }

    public Integer getParkingThreshold() {
        if (parkingThreshold == null) return DEFAULT_PARKING_CHANGE_THD;
        return parkingThreshold;
    }

    //Set the geomagnetic offset value of the parking space occupancy relative to the idle parking space
    //unit is mg
    public boolean setParkingThreshold(Integer parkThd) {
        if (parkThd <= MAX_PARKING_CHANGE_THD
                && parkThd >= MIN_PARKING_CHANGE_THD)
        {
            parkingThreshold = parkThd;
            return true;
        }
        else
        {
            return false;
        }
    }

    public Integer getParkingDelay() {
        if (parkingDelay == null) return DEFAULT_PARKING_DELAY_THD;
        return parkingDelay;
    }

    //If the setting continuously detects geomagnetic changes for more than parkDly * 10 seconds,
    //the device will generate a parking space occupancy event.
    public boolean setParkingDelay(Integer parkDly) {
        if (parkDly <= MAX_PARKING_DELAY_THD
                && parkDly >= MIN_PARKING_DELAY_THD)
        {
            parkingDelay = parkDly;
            return true;
        }
        else
        {
            return false;
        }
    }

    public Boolean isParkingTaged(){
        if (parkingTag == null) return false;
        return parkingTag == 1;
    }

    public Boolean isCalibrated() {
        if (calibration == null) return false;
        return calibration == 1;
    }

    public void setParkingTag(boolean tag) {

        this.parkingTag = tag ? 1:0;
    }

    public void setCalibration(boolean forceCalibration) {

        this.calibration = forceCalibration ? 1:0;
    }

    public void setMeasureInterval(Integer measureInterval) {
        this.measureInterval = measureInterval;
    }
    public Integer getMeasureInterval()
    {
        return measureInterval;
    }

    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateConfigNum = super.updateConfig(dicts);

        if (dicts.has(JSON_SENSOR_TYPE_GEO_PTHD))
        {
            parkingThreshold = dicts.getInt(JSON_SENSOR_TYPE_GEO_PTHD);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_MEASURE_INTERVAL))
        {
            measureInterval = dicts.getInt(JSON_SENSOR_TYPE_MEASURE_INTERVAL);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_GEO_PDLY))
        {
            parkingDelay = (Integer) dicts.get(JSON_SENSOR_TYPE_GEO_PDLY);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_GEO_TAG))
        {
            parkingTag = (Integer) dicts.get(JSON_SENSOR_TYPE_GEO_TAG);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_GEO_FCL))
        {
            calibration = (Integer) dicts.get(JSON_SENSOR_TYPE_GEO_FCL);
            nUpdateConfigNum++;
        }

        return nUpdateConfigNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject configDicts = super.toJSONObject();


        if (measureInterval != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_MEASURE_INTERVAL, measureInterval);
        }

        if (parkingThreshold != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_GEO_PTHD, parkingThreshold);
        }

        if (parkingDelay != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_GEO_PDLY, parkingDelay);
        }

        if (parkingTag != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_GEO_TAG, parkingTag);
        }

        if (calibration != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_GEO_FCL, calibration);
        }

        return configDicts;
    }
}

