package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBSensorType;

public class KBRecordPIR extends KBRecordBase {
    public long utcTime;

    public byte pirIndication;   // bit 0: is PIR alert

    public static int PIR_RECORD_LEN = 5;


    @Override
    public int getRecordLen() {
        return PIR_RECORD_LEN;
    }

    @Override
    public int getSenorType()
    {
        return KBSensorType.Light;
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

        pirIndication = sensorDataRsp[nRecordPtr];
        nRecordPtr += 1;

        return true;
    }
}
