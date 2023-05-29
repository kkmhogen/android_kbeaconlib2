package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBSensorType;
import com.kkmcn.kbeaconlib2.KBUtility;

public class KBRecordHumidity extends KBRecordBase {
    public long utcTime;

    public float temperature;

    public float humidity;

    public static int HT_RECORD_LEN = 8;

    @Override
    public int getRecordLen() {
        return HT_RECORD_LEN;
    }

    @Override
    public int getSenorType()
    {
        return KBSensorType.HTHumidity;
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

        temperature = KBUtility.signedBytes2Float(sensorDataRsp[nRecordPtr], sensorDataRsp[nRecordPtr+1]);
        nRecordPtr += 2;

        humidity = KBUtility.signedBytes2Float(sensorDataRsp[nRecordPtr], sensorDataRsp[nRecordPtr+1]);
        nRecordPtr += 2;


        return true;
    }
}
