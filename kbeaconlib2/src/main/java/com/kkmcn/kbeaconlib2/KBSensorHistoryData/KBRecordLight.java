package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBSensorType;

public class KBRecordLight extends KBRecordBase {
    public long utcTime;

    public byte lightType;   // 0: pir , 0x1: light record

    public int lightLevel;

    public static final int LIGHT_RECORD_LEN = 8;

    @Override
    public int getRecordLen() {
        return LIGHT_RECORD_LEN;
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

        //record type
        lightType = sensorDataRsp[nRecordPtr++];

        //reserved
        nRecordPtr++;

        //light level
        lightLevel = ((sensorDataRsp[nRecordPtr] & 0xFF) << 8);
        lightLevel += (sensorDataRsp[nRecordPtr + 1] & 0xFF);
        nRecordPtr += 2;


        return true;
    }
}
