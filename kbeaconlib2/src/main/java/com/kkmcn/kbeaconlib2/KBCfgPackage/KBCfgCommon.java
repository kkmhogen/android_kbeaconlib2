package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBException;
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;

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

    //configurable parameters
    public final static String  JSON_FIELD_DEV_NAME = "name";
    public final static String  JSON_FIELD_PWD = "pwd";
    public final static String  JSON_FIELD_MEA_PWR = "meaPwr";
    public final static String  JSON_FIELD_AUTO_POWER_ON = "atPwr";

    //support adv slot number
    private Integer maxAdvSlot;

    //support trigger number
    private Integer maxTriggerNum;

    private Integer basicCapability;

    private Integer trigCapability;

    private Integer maxTxPower;

    private Integer minTxPower;

    private String model;

    private String version;

    private String hversion;

    ////////////////////can be configruation able///////////////////////
    private Integer refPower1Meters;   //received RSSI at 1 meters

    private String password;

    private String name;

    private Boolean alwaysPowerOn; //beacon automatic start advertisement after powen on

    public Integer getMaxAdvSlot()
    {
        return maxAdvSlot;
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

    //is support trigger
    public boolean isSupportTrigger(int nTriggerType)
    {
        int nTriggerMask = (1 << (nTriggerType-1));
        return ((trigCapability & nTriggerMask) > 0);
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

    public void setAlwaysPowerOn(Boolean nAutoAdvAfterPowerOn) {
        alwaysPowerOn = nAutoAdvAfterPowerOn;
    }

    public int updateConfig(HashMap<String, Object> dicts) {
        int nUpdateParaNum = super.updateConfig(dicts);
        String strTempValue;

        strTempValue = (String) dicts.get(JSON_FIELD_BEACON_MODEL);
        if (strTempValue != null) {
            model = strTempValue;
            nUpdateParaNum++;
        }

        strTempValue = (String) dicts.get(JSON_FIELD_BEACON_VER);
        if (strTempValue != null) {
            version = strTempValue;
            nUpdateParaNum++;
        }

        strTempValue = (String) dicts.get(JSON_FIELD_BEACON_HVER);
        if (strTempValue != null) {
            hversion = strTempValue;
            nUpdateParaNum++;
        }

        Integer nTempValue = (Integer) dicts.get(JSON_FIELD_MAX_TX_PWR);
        if (nTempValue != null) {
            maxTxPower = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer) dicts.get(JSON_FIELD_MAX_SLOT_NUM);
        if (nTempValue != null) {
            maxAdvSlot = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer) dicts.get(JSON_FIELD_MAX_TRIGGER_NUM);
        if (nTempValue != null) {
            maxTriggerNum = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer) dicts.get(JSON_FIELD_MIN_TX_PWR);
        if (nTempValue != null) {
            minTxPower = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer) dicts.get(JSON_FIELD_BASIC_CAPABILITY);
        if (nTempValue != null) {
            basicCapability = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer) dicts.get(JSON_FIELD_TRIG_CAPABILITY);
        if (nTempValue != null) {
            trigCapability = nTempValue;
            nUpdateParaNum++;
        }

        //reference power
        nTempValue = (Integer) dicts.get(JSON_FIELD_MEA_PWR);
        if (nTempValue != null) {
            refPower1Meters = nTempValue;
            nUpdateParaNum++;
        }

        //password
        strTempValue = (String) dicts.get(JSON_FIELD_PWD);
        if (strTempValue != null) {
            password = strTempValue;
            nUpdateParaNum++;
        }

        //device name
        strTempValue = (String) dicts.get(JSON_FIELD_DEV_NAME);
        if (strTempValue != null) {
            name = strTempValue;
            nUpdateParaNum++;
        }

        //auto power on
        nTempValue = (Integer) dicts.get(JSON_FIELD_AUTO_POWER_ON);
        if (nTempValue != null) {
            alwaysPowerOn = nTempValue > 0;
            nUpdateParaNum++;
        }

        return nUpdateParaNum;
    }

    public HashMap<String, Object> toDictionary()
    {
        HashMap<String, Object> configDicts = super.toDictionary();

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

        return configDicts;
    }
}
