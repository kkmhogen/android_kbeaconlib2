package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBException;
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class KBCfgCommon extends KBCfgBase{
    public final static int  KB_CAPABILITY_KEY = 0x1;
    public final static int  KB_CAPABILITY_BEEP =  0x2;
    public final static int  KB_CAPABILITY_ACC = 0x4;
    public final static int  KB_CAPABILITY_TEMP = 0x8;
    public final static int  KB_CAPABILITY_HUMIDITY = 0x10;

    public final static int MAX_NAME_LENGTH = 18;

    public final static int MIN_REFERENCE_POWER = -100;
    public final static int MAX_REFERENCE_POWER = 10;
    public final static float MIN_ADV_PERIOD_MS = 100.0f;
    public final static float MAX_ADV_PERIOD_MS = 20000.0f;

    public final static String JSON_FIELD_MAX_SLOT_NUM = "maxSlot";
    public final static String JSON_FIELD_MAX_TRIGGER_NUM = "maxTg";
    public final static String  JSON_FIELD_BEACON_MODEL = "model";
    public final static String  JSON_FIELD_BEACON_VER = "ver";
    public final static String  JSON_FIELD_BEACON_HVER = "hver";
    public final static String  JSON_FIELD_MIN_TX_PWR = "minPwr";
    public final static String  JSON_FIELD_MAX_TX_PWR = "maxPwr";
    public final static String  JSON_FIELD_BASIC_CAPABILITY = "bCap";
    public final static String JSON_FIELD_TRIG_CAPABILITY = "trCap";
    public final static String JSON_FIELD_BATTERY_PERCENT = "btPt";

    //channel mask
    public final static int ADV_CHANNEL_37_MASK = 0x4;
    public final static int ADV_CHANNEL_38_MASK = 0x2;
    public final static int ADV_CHANNEL_39_MASK = 0x1;

    //configurable parameters
    public final static String  JSON_FIELD_DEV_NAME = "name";
    public final static String  JSON_FIELD_PWD = "pwd";
    public final static String  JSON_FIELD_MEA_PWR = "meaPwr";
    public final static String  JSON_FIELD_AUTO_POWER_ON = "atPwr";
    public final static String  JSON_FIELD_MAX_ADV_PERIOD = "maxPrd";
    public final static String  JSON_FIELD_CHANNEL_MASK = "chMsk";

    //flash led interval
    public final static String  JSON_FIELD_BLINK_LED_INTERVAL = "led";
    //low battery flash only
    public final static String  JSON_FIELD_LED_BLINK_ONLY_IN_LOW_BATTERY = "lwBlk";

    //support adv slot number
    private Integer maxAdvSlot;

    //support trigger number
    private Integer maxTriggerNum;

    //support max advertisement period
    private Float maxAdvPeriod;

    private Integer basicCapability;

    private Integer trigCapability;

    private Integer maxTxPower;

    private Integer minTxPower;

    private Integer batteryPercent;

    private String model;

    private String version;

    private String hversion;

    ////////////////////can be configruation able///////////////////////
    private Integer refPower1Meters;   //received RSSI at 1 meters

    private String password;

    private String name;

    //beacon automatic start advertisement after power on
    private Boolean alwaysPowerOn;

    //advertisement channel mask
    private Integer advChanelMask;

    //led flash when power on
    private Integer alwaysLedBlinkInterval;
    private Boolean lowBatteryLedBlinkOnly;

    public Integer getMaxAdvSlot()
    {
        return maxAdvSlot;
    }

    public Float getMaxAdvPeriod() {
        if (maxAdvPeriod != null) {
            return maxAdvPeriod;
        }
        return MAX_ADV_PERIOD_MS;
    }

    public Integer getMaxTriggerNum() {
        return maxTriggerNum;
    }

    //basic capability
    public Integer getBasicCapability()
    {
        return basicCapability;
    }

    //is the device support iBeacon
    public boolean isSupportIBeacon()
    {
        int nAdvCapability = (basicCapability >> 8);
        return ((nAdvCapability >> (KBAdvType.IBeacon -1)) & 0x1) > 0;
    }

    //is the device support URL
    public boolean isSupportEddyURL()
    {
        int nAdvCapability = (basicCapability >> 8);
        return ((nAdvCapability >> (KBAdvType.EddyURL -1)) & 0x1) > 0;
    }

    //is the device support TLM
    public boolean isSupportEddyTLM()
    {
        int nAdvCapability = (basicCapability >> 8);
        return ((nAdvCapability >> (KBAdvType.EddyTLM -1)) & 0x1) > 0;
    }

    //is the device support UID
    public boolean isSupportEddyUID()
    {
        int nAdvCapibility = (basicCapability >> 8);
        return ((nAdvCapibility >> (KBAdvType.EddyUID -1)) & 0x1) > 0;
    }

    //support kb sensor
    public boolean isSupportKBSensor()
    {
        int nAdvCapibility = (basicCapability >> 8);
        return ((nAdvCapibility >> (KBAdvType.Sensor -1)) & 0x1) > 0;
    }

    //support BLE5 LongRange
    public boolean isSupportBLELongRangeAdv()
    {
        int nBALE5Capability = (basicCapability >> 16);
        return (nBALE5Capability & 0x2) > 0;
    }

    //support BLE5 2MBPS
    public boolean isSupportBLE2MBps()
    {
        int nBALE5Capability = (basicCapability >> 16);
        return (nBALE5Capability & 0x4) > 0;
    }

    //support security DFU
    public boolean isSupportSecurityDFU()
    {
        int nBALE5Capability = (basicCapability >> 16);
        return (nBALE5Capability & 0x8) > 0;
    }

    //support kb system adv
    public boolean isSupportKBSystem()
    {
        int nAdvCapability = (basicCapability >> 8);
        return ((nAdvCapability >> (KBAdvType.System -1)) & 0x1) > 0;
    }

    //is support button
    public boolean isSupportButton()
    {
        return ((basicCapability & 0x1) > 0);
    }

    //is support beep
    public boolean isSupportBeep()
    {
        return ((basicCapability & 0x2) > 0);
    }

    //is support acc sensor
    public boolean isSupportAccSensor()
    {
        return ((basicCapability & 0x4) > 0);
    }

    //is support humidity sensor
    public boolean isSupportHumiditySensor()
    {
        return ((basicCapability & 0x8) > 0);
    }

    //is support history record
    public boolean isSupportHistoryRecord()
    {
        return ((basicCapability & 0x100000) > 0);
    }

    //is support cutoff sensor
    public boolean isSupportCutoffSensor()
    {
        return ((basicCapability & 0x10) > 0);
    }

    //is support PIR sensor
    public boolean isSupportPIRSensor()
    {
        return ((basicCapability & 0x20) > 0);
    }

    //is support light sensor
    public boolean isSupportLightSensor()
    {
        return ((basicCapability & 0x40) > 0);
    }

    //is support voc sensor
    public boolean isSupportVOCSensor()
    {
        return ((basicCapability & 0x80) > 0);
    }

    //is support nox sensor
    public boolean isSupportCO2Sensor()
    {
        return ((basicCapability & 0x1000000) > 0);
    }

    //is support trigger
    public boolean isSupportTrigger(int nTriggerType)
    {
        int nTriggerMask = (1 << (nTriggerType-1));
        return ((trigCapability & nTriggerMask) > 0);
    }

    //is support channel mask
    public boolean isSupportAdvChannelMask()
    {
        return ((basicCapability & 0x200000) > 0);
    }

    //trigger capability
    public Integer getTrigCapability()
    {
        return trigCapability;
    }

    public Integer getMaxTxPower()
    {
        return maxTxPower;
    }

    public Integer getMinTxPower()
    {
        return minTxPower;
    }

    public Integer getRefPower1Meters()
    {
        return refPower1Meters;
    }

    public String getModel()
    {
        return model;
    }

    public String getVersion()
    {
        return version;
    }

    public String getHardwareVersion()
    {
        return hversion;
    }

    public String getName()
    {
        return name;
    }

    public Integer getAlwaysLedBlinkInterval() {
        return alwaysLedBlinkInterval;
    }

    public Boolean isLowBatteryBlinkOnly(){
        return lowBatteryLedBlinkOnly;
    }

    public Integer getAdvChanelMask() {
        return advChanelMask;
    }

    public KBCfgCommon()
    {
    }

    public Boolean isAlwaysPowerOn()
    {
        return alwaysPowerOn;
    }

    public ArrayList<Integer> getSupportedSensorArray()
    {
        ArrayList<Integer> sensorArray = new ArrayList<>(3);
        if (isSupportHumiditySensor()){
            sensorArray.add(KB_CAPABILITY_HUMIDITY);
        }
        if (isSupportAccSensor()){
            sensorArray.add(KB_CAPABILITY_ACC);
        }

        return sensorArray;
    }

    public boolean setRefPower1Meters( Integer nRefPower1Meters) {
        if (nRefPower1Meters < -10 && nRefPower1Meters > -100) {
            refPower1Meters = nRefPower1Meters;
            return true;
        } else {
            return false;
        }
    }

    public boolean setPassword(String strPwd) {
        if (strPwd.length() >= 8 && strPwd.length() <= 16) {
            password = strPwd;
            return true;
        } else {
            return false;
        }
    }

    public boolean setName(String strName)  {
        if (strName.length() <= MAX_NAME_LENGTH) {
            name = strName;
            return true;
        } else {
            return false;
        }
    }

    public void setAlwaysFlashLedInterval(Integer alwaysFlashLedInterval) {
        this.alwaysLedBlinkInterval = alwaysFlashLedInterval;
    }

    public void setLowBatteryLedBlinkOnly(Boolean lowBatteryFlash) {
        this.lowBatteryLedBlinkOnly = lowBatteryFlash;
    }

    public void setAdvChanelMask(Integer advChanelMask) {
        this.advChanelMask = advChanelMask;
    }

    public void setAlwaysPowerOn(Boolean nAutoAdvAfterPowerOn) {
        alwaysPowerOn = nAutoAdvAfterPowerOn;
    }

    public int updateConfig(JSONObject dicts) throws JSONException {
        int nUpdateParaNum = super.updateConfig(dicts);
        String strTempValue;

        if (dicts.has(JSON_FIELD_BEACON_MODEL)) {
            model = (String) dicts.get(JSON_FIELD_BEACON_MODEL);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_BEACON_VER)) {
            version = (String) dicts.get(JSON_FIELD_BEACON_VER);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_BEACON_HVER)) {
            hversion = (String) dicts.get(JSON_FIELD_BEACON_HVER);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_MAX_TX_PWR)) {
            maxTxPower = (Integer) dicts.get(JSON_FIELD_MAX_TX_PWR);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_MAX_SLOT_NUM)) {
            maxAdvSlot = (Integer) dicts.get(JSON_FIELD_MAX_SLOT_NUM);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_MAX_TRIGGER_NUM)) {
            maxTriggerNum = (Integer) dicts.get(JSON_FIELD_MAX_TRIGGER_NUM);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_MAX_ADV_PERIOD)) {
            maxAdvPeriod = parseFloat(dicts.get(JSON_FIELD_MAX_ADV_PERIOD));
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_MIN_TX_PWR)) {
            minTxPower = (Integer) dicts.get(JSON_FIELD_MIN_TX_PWR);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_BASIC_CAPABILITY)) {
            basicCapability = (Integer) dicts.get(JSON_FIELD_BASIC_CAPABILITY);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_TRIG_CAPABILITY)) {
            trigCapability = (Integer) dicts.get(JSON_FIELD_TRIG_CAPABILITY);
            nUpdateParaNum++;
        }

        //reference power
        if (dicts.has(JSON_FIELD_MEA_PWR)) {
            refPower1Meters = (Integer) dicts.get(JSON_FIELD_MEA_PWR);
            nUpdateParaNum++;
        }

        //password
        if (dicts.has(JSON_FIELD_PWD)) {
            password = (String) dicts.get(JSON_FIELD_PWD);
            nUpdateParaNum++;
        }

        //device name
        if (dicts.has(JSON_FIELD_DEV_NAME)) {
            name = (String) dicts.get(JSON_FIELD_DEV_NAME);
            nUpdateParaNum++;
        }

        //auto power on
        if (dicts.has(JSON_FIELD_AUTO_POWER_ON)) {
            alwaysPowerOn = dicts.getInt(JSON_FIELD_AUTO_POWER_ON) > 0;
            nUpdateParaNum++;
        }

        //battery percent
        if (dicts.has(JSON_FIELD_BATTERY_PERCENT)) {
            batteryPercent =  (Integer) dicts.get(JSON_FIELD_BATTERY_PERCENT);
            nUpdateParaNum++;
        }

        //adv channel mask
        if (dicts.has(JSON_FIELD_CHANNEL_MASK)) {
            advChanelMask =  (Integer) dicts.get(JSON_FIELD_CHANNEL_MASK);
            nUpdateParaNum++;
        }

        //always led flash
        if (dicts.has(JSON_FIELD_BLINK_LED_INTERVAL)) {
            alwaysLedBlinkInterval =  (Integer) dicts.get(JSON_FIELD_BLINK_LED_INTERVAL);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_LED_BLINK_ONLY_IN_LOW_BATTERY)) {
            lowBatteryLedBlinkOnly = (Integer) dicts.get(JSON_FIELD_LED_BLINK_ONLY_IN_LOW_BATTERY) > 0;
            nUpdateParaNum++;
        }

        return nUpdateParaNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject configDicts = super.toJSONObject();

        //reference power
        if (refPower1Meters != null) {
            configDicts.put(JSON_FIELD_MEA_PWR, refPower1Meters);
        }

        //password
        if (password != null && password.length() >= 8 && password.length() <= 16) {
            configDicts.put(JSON_FIELD_PWD, password);
        }

        //device name
        if (name != null) {
            configDicts.put(JSON_FIELD_DEV_NAME, name);
        }

        //auto power
        if (alwaysPowerOn != null) {
            configDicts.put(JSON_FIELD_AUTO_POWER_ON, alwaysPowerOn? 1 : 0);
        }

        //is always flash led
        if (alwaysLedBlinkInterval != null){
            configDicts.put(JSON_FIELD_BLINK_LED_INTERVAL, alwaysLedBlinkInterval);
        }

        if (lowBatteryLedBlinkOnly != null){
            configDicts.put(JSON_FIELD_LED_BLINK_ONLY_IN_LOW_BATTERY, lowBatteryLedBlinkOnly ? 1 : 0);
        }

        //channel mask
        if (advChanelMask != null){
            configDicts.put(JSON_FIELD_CHANNEL_MASK, advChanelMask);
        }

        return configDicts;
    }

    public Integer getBatteryPercent() {
        return batteryPercent;
    }

    public String getPassword() {
        return password;
    }
}
