package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBSensorType;

public class KBRecordVOC extends KBRecordBase {
    public long utcTime;

    public short vocIndex;
    
    public short noxIndex;

    public static int VOC_RECORD_LEN = 8;

    @Override
    public int getRecordLen() {
        return VOC_RECORD_LEN;
    }

    @Override
    public int getSenorType()
    {
        return KBSensorType.VOC;
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

        vocIndex = (short)((sensorDataRsp[nRecordPtr++] & 0xFF) << 8);
        vocIndex += (short)(sensorDataRsp[nRecordPtr++] & 0xFF);

        noxIndex = (short)((sensorDataRsp[nRecordPtr++] & 0xFF) << 8);
        noxIndex += (short)(sensorDataRsp[nRecordPtr++] & 0xFF);

        return true;
    }
}
