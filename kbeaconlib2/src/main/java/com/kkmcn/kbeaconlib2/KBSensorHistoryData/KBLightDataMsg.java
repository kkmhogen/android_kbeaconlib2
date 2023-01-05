package com.kkmcn.kbeaconlib2.KBSensorHistoryData;
import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBeacon;

import java.util.ArrayList;

public class KBLightDataMsg extends KBSensorDataMsgBase{
    public class ReadLightSensorDataRsp  extends Object
    {
        public Long readDataNextPos;

        public ArrayList<KBLightRecord> readDataRspList;
    };

    public static final int KBSensorDataTypeLight = 0x10;
    public static final long MIN_UTC_TIME_SECONDS = 946080000;
    public static final int LIGHT_RECORD_LEN = 8;

    public static final int LUX_TYPE_MASK = 0x2;
    public static final int PIR_TYPE_MASK = 0x1;

    public int getSensorDataType()
    {
        return KBSensorDataTypeLight;
    }

    public Object parseSensorDataResponse(final KBeacon beacon, int nDataPtr, byte[] sensorDataRsp)
    {
        //sensor data type
        int nReadIndex = nDataPtr;
        if (sensorDataRsp[nReadIndex] != KBSensorDataTypeLight)
        {
            return null;
        }
        nReadIndex++;

        //next read data pos
        KBLightDataMsg.ReadLightSensorDataRsp readDataRsp = new KBLightDataMsg.ReadLightSensorDataRsp();
        readDataRsp.readDataNextPos = ByteConvert.bytesTo4Long(sensorDataRsp, nReadIndex);
        nReadIndex += 4;

        //check payload length
        int nPayLoad = (sensorDataRsp.length - nReadIndex);
        if (nPayLoad % LIGHT_RECORD_LEN != 0)
        {
            return null;
        }

        //read record
        readDataRsp.readDataRspList = new ArrayList<>(30);
        int nTotalRecordNum = nPayLoad / LIGHT_RECORD_LEN;
        int nRecordPtr = nReadIndex;
        for (int i = 0; i < nTotalRecordNum; i++)
        {
            KBLightRecord record = new KBLightRecord();

            //utc time
            record.mUtcTime = ByteConvert.bytesTo4Long(sensorDataRsp, nRecordPtr);
            if (record.mUtcTime < MIN_UTC_TIME_SECONDS)
            {
                record.mUtcTime += mUtcOffset;
            }
            nRecordPtr += 4;

            //record type
            record.mType = sensorDataRsp[nRecordPtr++];

            //pir indication
            if ((record.mType & PIR_TYPE_MASK) > 0) {
                record.mPirIndication = sensorDataRsp[nRecordPtr];
            }else{
                record.mPirIndication = -1;
            }
            nRecordPtr++;

            //light level
            if ((record.mType & LUX_TYPE_MASK) > 0) {
                record.mLightLevel = ((sensorDataRsp[nRecordPtr] & 0xFF) << 8);
                record.mLightLevel += (sensorDataRsp[nRecordPtr + 1] & 0xFF);
            }else{
                record.mLightLevel = -1;
            }
            nRecordPtr += 2;

            readDataRsp.readDataRspList.add(record);
        }

        return readDataRsp;
    }
}

