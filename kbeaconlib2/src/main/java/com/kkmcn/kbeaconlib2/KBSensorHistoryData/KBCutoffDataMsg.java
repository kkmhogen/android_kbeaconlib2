package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBeacon;
import com.kkmcn.kbeaconlib2.UTCTime;

import java.util.ArrayList;

public class KBCutoffDataMsg extends KBSensorDataMsgBase{
    public class ReadDoorSensorDataRsp  extends Object
    {
        public Long readDataNextPos;

        public ArrayList<KBCutoffRecord> readDataRspList;
    };

    public static final int KBSensorDataTypeCutoff = 4;
    public static final long MIN_UTC_TIME_SECONDS = 946080000;
    public static int CUT_OFF_RECORD_LEN = 5;

    public int getSensorDataType()
    {
        return KBSensorDataTypeCutoff;
    }

    public Object parseSensorDataResponse(final KBeacon beacon, int nDataPtr, byte[] sensorDataRsp)
    {
        //sensor data type
        int nReadIndex = nDataPtr;
        if (sensorDataRsp[nReadIndex] != KBSensorDataTypeCutoff)
        {
            return null;
        }
        nReadIndex++;

        //next read data pos
        ReadDoorSensorDataRsp readDataRsp = new ReadDoorSensorDataRsp();
        readDataRsp.readDataNextPos = ByteConvert.bytesTo4Long(sensorDataRsp, nReadIndex);
        nReadIndex += 4;

        //check payload length
        int nPayLoad = (sensorDataRsp.length - nReadIndex);
        if (nPayLoad % CUT_OFF_RECORD_LEN != 0)
        {
            return null;
        }

        //read record
        readDataRsp.readDataRspList = new ArrayList<>(30);
        int nTotalRecordNum= nPayLoad / CUT_OFF_RECORD_LEN;
        int nRecordPtr = nReadIndex;
        for (int i = 0; i < nTotalRecordNum; i++)
        {
            KBCutoffRecord record = new KBCutoffRecord();

            //utc time
            record.mUtcTime = ByteConvert.bytesTo4Long(sensorDataRsp, nRecordPtr);
            if (record.mUtcTime < MIN_UTC_TIME_SECONDS)
            {
                record.mUtcTime += mUtcOffset;
            }
            nRecordPtr += 4;

            record.mCutoffFlag = sensorDataRsp[nRecordPtr];
            nRecordPtr += 1;

            readDataRsp.readDataRspList.add(record);
        }

        return readDataRsp;
    }
}
