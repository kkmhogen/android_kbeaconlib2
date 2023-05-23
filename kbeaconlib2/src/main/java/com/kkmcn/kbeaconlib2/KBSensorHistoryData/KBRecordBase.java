package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

public abstract class KBRecordBase {

    public static final long MIN_UTC_TIME_SECONDS = 946080000;

    abstract public int getRecordLen();

    abstract public int getSenorType();

    abstract public boolean parseSensorDataResponse(long utcOffset, int nDataPtr, byte[] sensorDataRsp);
}
