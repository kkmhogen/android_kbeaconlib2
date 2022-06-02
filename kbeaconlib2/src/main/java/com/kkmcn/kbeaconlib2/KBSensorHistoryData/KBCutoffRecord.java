package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

public class KBCutoffRecord {
    public long mUtcTime;
    public byte mCutoffFlag;   // bit 0: is cutoff enable, bit 1: is device was unplug
}
