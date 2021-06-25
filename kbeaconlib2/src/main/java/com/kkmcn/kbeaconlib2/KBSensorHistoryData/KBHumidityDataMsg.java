package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBUtility;
import com.kkmcn.kbeaconlib2.KBeacon;
import com.kkmcn.kbeaconlib2.UTCTime;

import java.util.ArrayList;

public class KBHumidityDataMsg extends KBSensorDataMsgBase{
    public class ReadHTSensorInfoRsp extends Object
    {
        public Integer totalRecordNumber;

        public Integer unreadRecordNumber;

        public Long readInfoUtcSeconds;
    };

    public class ReadHTSensorDataRsp  extends Object
    {
        public Long readDataNextPos;

        public ArrayList<KBHumidityRecord> readDataRspList;
    };

    public static final int KBSensorDataTypeHumidity = 2;
    public static final long MIN_UTC_TIME_SECONDS = 946080000;

    private long mUtcOffset;

    public int getSensorDataType()
    {
        return KBSensorDataTypeHumidity;
    }

    public Object parseSensorInfoResponse(final KBeacon beacon, int nDataPtr, byte[] sensorDataRsp)
    {
        if (sensorDataRsp.length -  nDataPtr < 8)
        {
            return null;
        }

        ReadHTSensorInfoRsp infoRsp = new ReadHTSensorInfoRsp();

        //total record number
        infoRsp.totalRecordNumber = (int) ByteConvert.bytesTo4Long(sensorDataRsp, nDataPtr);
        nDataPtr+=4;

        //total record number
        infoRsp.unreadRecordNumber = (int) ByteConvert.bytesTo4Long(sensorDataRsp, nDataPtr);
        nDataPtr+=4;

        //utc offset
        infoRsp.readInfoUtcSeconds = (Long) ByteConvert.bytesTo4Long(sensorDataRsp, nDataPtr );
        mUtcOffset = UTCTime.getUTCTimeSeconds() - infoRsp.readInfoUtcSeconds;
        nDataPtr += 4;

        return infoRsp;
    }

    public Object parseSensorDataResponse(final KBeacon beacon, int nDataPtr, byte[] sensorDataRsp)
    {
        //sensor data type
        int nReadIndex = nDataPtr;
        if (sensorDataRsp[nReadIndex] != KBSensorDataTypeHumidity)
        {
            return null;
        }
        nReadIndex++;

        //next read data pos
        ReadHTSensorDataRsp readDataRsp = new ReadHTSensorDataRsp();
        readDataRsp.readDataNextPos = ByteConvert.bytesTo4Long(sensorDataRsp, nReadIndex);
        nReadIndex += 4;

        //check payload length
        int nPayLoad = (sensorDataRsp.length - nReadIndex);
        if (nPayLoad % 8 != 0)
        {
          return null;
        }

        //read record
        readDataRsp.readDataRspList = new ArrayList<>(30);
        int nTotalRecordLen = nPayLoad / 8;
        int nRecordPtr = nReadIndex;
        for (int i = 0; i < nTotalRecordLen; i++)
        {
            KBHumidityRecord record = new KBHumidityRecord();

            //utc time
            record.mUtcTime = ByteConvert.bytesTo4Long(sensorDataRsp, nRecordPtr);
            if (record.mUtcTime < MIN_UTC_TIME_SECONDS)
            {
                record.mUtcTime += mUtcOffset;
            }
            nRecordPtr += 4;


            record.mTemperature = KBUtility.signedBytes2Float(sensorDataRsp[nRecordPtr], sensorDataRsp[nRecordPtr+1]);
            nRecordPtr += 2;

            record.mHumidity = KBUtility.signedBytes2Float(sensorDataRsp[nRecordPtr], sensorDataRsp[nRecordPtr+1]);
            nRecordPtr += 2;

            readDataRsp.readDataRspList.add(record);
        }

        return readDataRsp;
    }
}
