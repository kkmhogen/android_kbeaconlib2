package com.kkmcn.kbeaconlib2.KBCfgPackage;

import org.json.JSONException;
import org.json.JSONObject;

public class KBCfgSensorScan extends KBCfgSensorBase{
    public static final String JSON_SENSOR_TYPE_SCAN_MODE = "mode";
    public static final String JSON_SENSOR_TYPE_SCAN_RSSI = "rssi";
    public static final String JSON_SENSOR_TYPE_SCAN_DUR = "dur";
    public static final String JSON_SENSOR_TYPE_SCAN_MSK = "chMsk";

    public static final String JSON_SENSOR_TYPE_SCAN_INTERVAL = "itvl";

    public static final String JSON_SENSOR_TYPE_MOTION_SCAN_INTERVAL = "mItvl";

    public static final String JSON_SENSOR_TYPE_SCAN_DATA_ADV_SLOT = "aSlot";

    public static final String JSON_SENSOR_TYPE_SCAN_MAX = "max";

    public static final int MIN_FILTER_RSSI = -100;
    public static final int MAX_FILTER_RSSI = 10;

    public static final int  MIN_SCAN_DURATION = 1;
    public static final int MAX_SCAN_DURATION = 60000;

    public static final int MIN_SCAN_PERIPHERAL_COUNT = 1;
    public static final int MAX_SCAN_PERIPHERAL_COUNT = 100;

    public static final int MAX_SCAN_INTERVAL = 7200;

    //scanMode
    private Integer scanMode;

    private Integer scanRssi;

    private Integer scanDuration;

    private Integer scanInterval;

    private Integer motionScanInterval;

    private Integer scanResultAdvSlot;

    private Integer scanChanelMask;

    private Integer scanMax;

    public Integer getScanMode() {
        return scanMode;
    }

    //Scan peripheral device broadcast types, including:
    //KBAdvMode.Legacy(BLE4.0), KBAdvMode.LongRangeCodedS8(BLE5.0 PHY coded S8)
    //KBAdvMode.ExtendAdvertisement(BLE5.0)
    public void setScanMode(Integer scanMode) {
        if (scanMode == KBAdvMode.Legacy
                || scanMode == KBAdvMode.LongRangeCodedS8
                || scanMode == KBAdvMode.ExtendAdvertisement)
        {
            this.scanMode = scanMode;
        }
    }

    public Integer getScanRssi() {
        return this.scanRssi;
    }

    //Beacon can scan peripheral devices and support filtering based on RSSI.
    // For example, only scanning devices with RSSI>-45dBm.
    public void setScanRssi(Integer rssi) {
        if (rssi >= MIN_FILTER_RSSI && rssi <= MAX_FILTER_RSSI)
        {
            this.scanRssi = rssi;
        }
    }

    public Integer getScanDuration() {
        return scanDuration;
    }

    public Integer getScanInterval() {
        return scanInterval;
    }

    //scan interval unit is seconds. the scan interval time must > 2 * scan duration
    public void setScanInterval(Integer scanInterval) {
        if (scanInterval >= 0 && scanInterval <= MAX_SCAN_INTERVAL) {
            this.scanInterval = scanInterval;
        }
    }

    public void setScanResultAdvSlot(Integer scanResultAdvSlot) {
        this.scanResultAdvSlot = scanResultAdvSlot;
    }

    public Integer getScanResultAdvSlot() {
        return scanResultAdvSlot;
    }

    //scan interval when motion detected. the scan interval time must > 2 * scan duration
    public void setMotionScanInterval(Integer motionScanInterval) {
        this.motionScanInterval = motionScanInterval;
    }

    public Integer getMotionScanInterval() {
        return motionScanInterval;
    }

    //Duration of each Beacon scan of peripheral devices, unit is 10 ms
    //The maximum scanning duration per scan is 600 seconds
    public void setScanDuration(Integer scanDuration) {
        if (scanDuration >= MIN_SCAN_DURATION && scanDuration <= MAX_SCAN_DURATION) {
            this.scanDuration = scanDuration;
        }
    }

    public Integer getScanChanelMask() {
        return scanChanelMask;
    }

    //The scanning advertisement channel mask is 3 bit, channel 37(bit0), channel 38(bit1)
    // channel 39(bit2). if the chanel bit is 1, then the Beacon will not scan on the channel
    //for example, if the advChannelMask is 0x3(0B'011), then the beacon only scan BLE channel 37
    public void setScanChanelMask(Integer advChanelMask) {
        if (advChanelMask < 7) {
            this.scanChanelMask = advChanelMask;
        }
    }

    public Integer getScanMax() {
        return scanMax;
    }

    //The maximum number of peripheral devices during each scan
    // When the number of devices scanned exceed the scanMax value, then stop scanning.
    public void setScanMax(Integer scanMax) {
        if (scanMax >= MIN_SCAN_PERIPHERAL_COUNT && scanMax <= MAX_SCAN_PERIPHERAL_COUNT) {
            this.scanMax = scanMax;
        }
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

        if (dicts.has(JSON_SENSOR_TYPE_SCAN_INTERVAL))
        {
            scanInterval = (Integer) dicts.get(JSON_SENSOR_TYPE_SCAN_INTERVAL);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_MOTION_SCAN_INTERVAL))
        {
            motionScanInterval = (Integer) dicts.get(JSON_SENSOR_TYPE_MOTION_SCAN_INTERVAL);
            nUpdateConfigNum++;
        }

        if (dicts.has(JSON_SENSOR_TYPE_SCAN_DATA_ADV_SLOT))
        {
            scanResultAdvSlot = (Integer) dicts.get(JSON_SENSOR_TYPE_SCAN_DATA_ADV_SLOT);
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

        if (scanInterval != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_SCAN_INTERVAL, scanInterval);
        }

        if (motionScanInterval != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_MOTION_SCAN_INTERVAL, motionScanInterval);
        }

        if (scanResultAdvSlot != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_SCAN_DATA_ADV_SLOT, scanResultAdvSlot);
        }

        if (scanMax != null)
        {
            configDicts.put(JSON_SENSOR_TYPE_SCAN_MAX, scanMax);
        }

        return configDicts;
    }
}

