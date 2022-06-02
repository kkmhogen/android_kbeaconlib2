package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBeacon;

import java.util.ArrayList;

public class KBPIRDataMsg extends KBSensorDataMsgBase{
    public class ReadPIRSensorDataRsp  extends Object
    {
        public Long readDataNextPos;

        public ArrayList<KBPIRRecord> readDataRspList;
    };

    public static final int KBSensorDataTypePIR = 8;
    public static final long MIN_UTC_TIME_SECONDS = 946080000;
    public static int PIR_RECORD_LEN = 5;

    public int getSensorDataType()
    {
        return KBSensorDataTypePIR;
    }

    public Object parseSensorDataResponse(final KBeacon beacon, int nDataPtr, byte[] sensorDataRsp)
    {
        //sensor data type
        int nReadIndex = nDataPtr;
        if (sensorDataRsp[nReadIndex] != KBSensorDataTypePIR)
        {
            return null;
        }
        nReadIndex++;

        //next read data pos
        ReadPIRSensorDataRsp readDataRsp = new ReadPIRSensorDataRsp();
        readDataRsp.readDataNextPos = ByteConvert.bytesTo4Long(sensorDataRsp, nReadIndex);
        nReadIndex += 4;

        //check payload length
        int nPayLoad = (sensorDataRsp.length - nReadIndex);
        if (nPayLoad % PIR_RECORD_LEN != 0)
        {
            return null;
        }

        //read record
        readDataRsp.readDataRspList = new ArrayList<>(30);
        int nTotalRecordNum= nPayLoad / PIR_RECORD_LEN;
        int nRecordPtr = nReadIndex;
        for (int i = 0; i < nTotalRecordNum; i++)
        {
            KBPIRRecord record = new KBPIRRecord();

            //utc time
            record.mUtcTime = ByteConvert.bytesTo4Long(sensorDataRsp, nRecordPtr);
            if (record.mUtcTime < MIN_UTC_TIME_SECONDS)
            {
                record.mUtcTime += mUtcOffset;
            }
            nRecordPtr += 4;

            record.mPirIndication = sensorDataRsp[nRecordPtr];
            nRecordPtr += 1;

            readDataRsp.readDataRspList.add(record);
        }

        return readDataRsp;
    }
}
