package com.kkmcn.kbeaconlib2.KBCfgPackage;

import org.json.JSONException;
import org.json.JSONObject;

public class KBCfgSensorScan extends KBCfgSensorBase{
    public static final String JSON_SENSOR_TYPE_SCAN_MODE = "mode";
    public static final String JSON_SENSOR_TYPE_SCAN_RSSI = "rssi";
    public static final String JSON_SENSOR_TYPE_SCAN_DUR = "dur";
    public static final String JSON_SENSOR_TYPE_SCAN_MSK = "chMsk";
    public static final String JSON_SENSOR_TYPE_SCAN_MAX = "max";

    //scanMode
    private Integer scanMode;

    private Integer scanRssi;

    private Integer scanDuration;

    private Integer scanChanelMask;

    private Integer scanMax;

    public Integer getScanMode() {
        return scanMode;
    }

    public void setScanMode(Integer scanMode) {
        this.scanMode = scanMode;
    }

    public Integer getScanRssi() {
        return scanRssi;
    }

    public void setScanRssi(Integer scanRssi) {
        this.scanRssi = scanRssi;
    }

    public Integer getScanDuration() {
        return scanDuration;
    }

    public void setScanDuration(Integer scanDuration) {
        this.scanDuration = scanDuration;
    }

    public Integer getScanChanelMask() {
        return scanChanelMask;
    }


    //The advertisement channel mask is 3 bit (37,38,39 channel), if the msak bit is 0, then it not advertisement on the channel
    //for example, if the advChannelMask is 0x3, then the beacon only advertisement on channel 37
    public boolean setScanChanelMask(Integer advChanelMask) {
        if (advChanelMask < 7) {
            this.scanChanelMask = advChanelMask;
            return true;
        }else{
            return false;
        }
    }

    public Integer getScanMax() {
        return scanMax;
    }

    public void setScanMax(Integer scanMax) {
        this.scanMax = scanMax;
    }

    public KBCfgSensorScan()
    {
        super();
        sensorType = KBSensorType.SCAN;
    }

    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateConfigNum = super.updateConfig(dicts);

        if (dicts.has(JSON_SENSOR_TYPE_SCAN_MODE))
        {
            scanMode = dicts.getInt(JSON_SENSOR_TYPE_SCAN_MODE);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_SCAN_RSSI))
        {
            scanRssi = dicts.getInt(JSON_SENSOR_TYPE_SCAN_RSSI);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_SCAN_DUR))
        {
            scanDuration = (Integer) dicts.get(JSON_SENSOR_TYPE_SCAN_DUR);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_SCAN_MSK))
        {
            scanChanelMask = (Integer) dicts.get(JSON_SENSOR_TYPE_SCAN_MSK);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_SCAN_MAX))
        {
            scanMax = dicts.getInt(JSON_SENSOR_TYPE_SCAN_MAX);
            nUpdateConfigNum++;
        }

        return nUpdateConfigNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject configDicts = super.toJSONObject();


        if (scanMode != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_SCAN_MODE, scanMode);
        }

        if (scanRssi != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_SCAN_RSSI, scanRssi);
        }

        if (scanDuration != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_SCAN_DUR, scanDuration);
        }

        if (scanChanelMask != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_SCAN_MSK, scanChanelMask);
        }

        if (scanMax != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_SCAN_MAX, scanMax);
        }

        return configDicts;
    }
}

