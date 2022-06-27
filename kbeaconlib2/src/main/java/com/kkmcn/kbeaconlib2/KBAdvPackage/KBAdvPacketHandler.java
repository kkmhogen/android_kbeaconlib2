package com.kkmcn.kbeaconlib2.KBAdvPackage;

import android.bluetooth.le.ScanRecord;
import android.util.Log;

import com.kkmcn.kbeaconlib2.KBUtility;

import java.util.HashMap;


public class KBAdvPacketHandler {
    private static final int MIN_EDDY_URL_ADV_LEN = 3;
    private static final int MIN_EDDY_UID_ADV_LEN = 18;
    private static final int MIN_EDDY_TLM_ADV_LEN = 14;
    private static final int MIN_IBEACON_ADV_LEN = 0x17;
    private static final int MIN_SYSTEM_ADV_LEN = 11;
    private static final int MIN_SENSOR_ADV_LEN = 3;

    private static final String LOG_TAG = "KBAdvPacketHandler";

    private Integer batteryPercent;

    private int filterAdvType;

    private HashMap<String, KBAdvPacketBase> mAdvPackets;

    static private HashMap<String, Class> kbAdvPacketTypeObjects;

    static{
        kbAdvPacketTypeObjects = new HashMap<>(5);
        kbAdvPacketTypeObjects.put(String.valueOf(KBAdvType.Sensor), KBAdvPacketSensor.class);
        kbAdvPacketTypeObjects.put(String.valueOf(KBAdvType.EddyURL), KBAdvPacketEddyURL.class);
        kbAdvPacketTypeObjects.put(String.valueOf(KBAdvType.EddyTLM), KBAdvPacketEddyTLM.class);
        kbAdvPacketTypeObjects.put(String.valueOf(KBAdvType.EddyUID), KBAdvPacketEddyUID.class);
        kbAdvPacketTypeObjects.put(String.valueOf(KBAdvType.IBeacon), KBAdvPacketIBeacon.class);
        kbAdvPacketTypeObjects.put(String.valueOf(KBAdvType.System), KBAdvPacketSystem.class);
    }

    public KBAdvPacketHandler()
    {
        mAdvPackets = new HashMap<>(5);
    }

    public KBAdvPacketBase[] advPackets()
    {
        KBAdvPacketBase[] advArrays = new KBAdvPacketBase[mAdvPackets.size()];
        mAdvPackets.values().toArray(advArrays);

        return advArrays;
    }

    public void setAdvTypeFilter(int filterAdvType) {
        this.filterAdvType = filterAdvType;
    }

    public Integer getBatteryPercent()
    {
        return batteryPercent;
    }

    public KBAdvPacketBase getAdvPacket(int nAdvType)
    {
        return mAdvPackets.get(String.valueOf(nAdvType));
    }

    public void removeAdvPacket()
    {
        this.mAdvPackets.clear();
    }

    public boolean parseAdvPacket(ScanRecord record, int rssi, String name) {
        int nAdvType = KBAdvType.AdvNull;
        byte[] beaconData = null;
        boolean bParseDataRslt = false;

        if (record.getManufacturerSpecificData() != null) {
            beaconData = record.getManufacturerSpecificData(KBUtility.APPLE_MANUFACTURE_ID);
            if (beaconData != null) {
                if (beaconData.length == MIN_IBEACON_ADV_LEN
                        && beaconData[0] == 0x2 && beaconData[1] == 0x15) {
                    nAdvType = KBAdvType.IBeacon;
                }
            }
            else
            {
                beaconData = record.getManufacturerSpecificData(KBUtility.KKM_MANUFACTURE_ID);
                if (beaconData != null) {
                    if (beaconData[0] == 0x21 && beaconData.length >= MIN_SENSOR_ADV_LEN) {
                        nAdvType = KBAdvType.Sensor;
                    }else if (beaconData[0] == 0x22 && beaconData.length >= MIN_SYSTEM_ADV_LEN) {
                        nAdvType = KBAdvType.System;
                    }
                }
            }
        }
        if (record.getServiceData() != null) {
            byte[] eddyData = record.getServiceData(KBUtility.PARCE_UUID_EDDYSTONE);
            if (eddyData != null) {
                beaconData = eddyData;
                if (eddyData[0] == 0x10 && eddyData.length >= MIN_EDDY_URL_ADV_LEN) {
                    nAdvType = KBAdvType.EddyURL;
                } else if (eddyData[0] == 0x0 && eddyData.length >= MIN_EDDY_UID_ADV_LEN) {
                    nAdvType = KBAdvType.EddyUID;
                } else if (eddyData[0] == 0x20 && eddyData.length >= MIN_EDDY_TLM_ADV_LEN) {
                    nAdvType = KBAdvType.EddyTLM;
                } else if (eddyData[0] == 0x21 && eddyData.length >= MIN_SENSOR_ADV_LEN) {
                    nAdvType = KBAdvType.Sensor;
                }else if (eddyData[0] == 0x22 && eddyData.length >= MIN_SYSTEM_ADV_LEN) {
                    nAdvType = KBAdvType.System;
                }else{
                    nAdvType = KBAdvType.AdvNull;
                }
            }
        }
        if ((filterAdvType & nAdvType) == 0)
        {
            return false;
        }

        byte[] byExtenData = record.getServiceData(KBUtility.PARCE_UUID_EXT_DATA);
        if (byExtenData != null && byExtenData.length > 2) {
            batteryPercent = (int) (byExtenData[0] & 0xFF);
            if (batteryPercent > 100){
                batteryPercent = 100;
            }
        }

        if (nAdvType != KBAdvType.AdvNull) {
            String strAdvTypeKey = String.valueOf(nAdvType);
            KBAdvPacketBase advPacket = mAdvPackets.get(strAdvTypeKey);
            boolean bNewObj = false;
            if (advPacket == null) {
                Class classNewObj = kbAdvPacketTypeObjects.get(strAdvTypeKey);
                try {
                    if (classNewObj != null) {
                        advPacket = (KBAdvPacketBase) classNewObj.newInstance();
                    }
                } catch (Exception excpt) {
                    excpt.printStackTrace();
                    Log.e(LOG_TAG, "create adv packet class failed");
                    return false;
                }
                bNewObj = true;
            }

            if (advPacket != null && advPacket.parseAdvPacket(beaconData)) {
                advPacket.updateBasicInfo(rssi);
                if (bNewObj) {
                    mAdvPackets.put(strAdvTypeKey, advPacket);
                }
                bParseDataRslt = true;
            }
        }

        return bParseDataRslt;
    }
}
