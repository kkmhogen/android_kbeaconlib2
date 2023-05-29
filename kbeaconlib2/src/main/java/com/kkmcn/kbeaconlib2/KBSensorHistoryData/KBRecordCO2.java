package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBSensorType;
import com.kkmcn.kbeaconlib2.KBUtility;

public class KBRecordCO2 extends KBRecordBase {
    public long utcTime;

    public short CO2;

    public float temperature;

    public float humidity;

    public static int CO2_RECORD_LEN = 10;

    @Override
    public int getRecordLen() {
        return CO2_RECORD_LEN;
    }

    @Override
    public int getSenorType()
    {
        return KBSensorType.CO2;
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

        CO2 = (short)((sensorDataRsp[nRecordPtr++] & 0xFF) << 8);
        CO2 += (short)(sensorDataRsp[nRecordPtr++] & 0xFF);

        temperature = KBUtility.signedBytes2Float(sensorDataRsp[nRecordPtr], sensorDataRsp[nRecordPtr+1]);
        nRecordPtr += 2;

        humidity = KBUtility.signedBytes2Float(sensorDataRsp[nRecordPtr], sensorDataRsp[nRecordPtr+1]);
        nRecordPtr += 2;

        return true;
    }
}
