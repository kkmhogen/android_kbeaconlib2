package com.kkmcn.kbeaconlib2.KBCfgPackage;

public class KBSleepTime {
    public byte mSleepStartHour;
    public byte mSleepStartMinute;
    public byte mSleepEndHour;
    public byte mSleepEndMinute;

    public KBSleepTime()
    {
        mSleepStartHour = mSleepStartMinute = mSleepEndHour = mSleepEndMinute = 0;
    }
}
