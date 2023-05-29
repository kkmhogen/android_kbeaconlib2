package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBSensorType;

public class KBRecordCutoff extends KBRecordBase {
    public long utcTime;

    public byte cutoffFlag;   // bit 0: is cutoff enable, bit 1: is device was unplug

    public static int CUT_OFF_RECORD_LEN = 5;

    @Override
    public int getRecordLen() {
        return CUT_OFF_RECORD_LEN;
    }

    @Override
    public int getSenorType()
    {
        return KBSensorType.Cutoff;
    }

    @Override
    public boolean parseSensorDataResponse(long utcOffset, int nDataPtr, byte[] sensorDataRsp)
    {
        int nRecordPtr = nDataPtr;

        utcTime = ByteConvert.bytesTo4Long(sensorDataRsp, nRecordPtr);
        if (utcTime < MIN_UTC_TIME_SECONDS)
        {
            utcTime += utcOffset;
        }
        nRecordPtr += 4;

        cutoffFlag = sensorDataRsp[nRecordPtr];
        nRecordPtr += 1;

        return true;
    }
}
