package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

public class KBLightRecord {
    public long mUtcTime;

    public byte mType;   // 0x1: include pir , 0x2: include light level

    public int mPirIndication;

    public int mLightLevel;
}
